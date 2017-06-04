package org.supla.android.listview;

/*
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 Author: Przemyslaw Zygmunt p.zygmunt@acsoftware.pl [AC SOFTWARE]
 */

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.supla.android.Trace;
import org.supla.android.db.Channel;
import java.util.ArrayList;


public class ListViewCursorAdapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;
    private ArrayList<SectionItem> Sections;
    private int currentSectionIndex;

    public static final int TYPE_CHANNEL = 0;
    public static final int TYPE_SECTION = 1;


    public class SectionItem {

        private String caption;
        private int position;
        private SectionLayout view;

        public SectionItem(int position, String caption) {
            this.position = position;
            this.caption = caption;
        }

        public int getPosition() {
            return position;
        }

        public String getCaption() {
            return caption;
        }

        public SectionLayout getView() {
            return view;
        }

        public void setView(SectionLayout view) {
            this.view = view;
        }

    }

    public ListViewCursorAdapter(Context context, Cursor cursor) {
        super();

        currentSectionIndex = 0;
        Sections = new ArrayList<SectionItem>();
        setCursor(cursor);
        this.context = context;

    }

    private boolean SectionsDiff(ArrayList<SectionItem> S1, ArrayList<SectionItem> S2) {

        if ( S1.size() != S2.size() )
            return true;

        for(int a=0;a<S1.size();a++) {

            SectionItem s1 = S1.get(a);
            SectionItem s2 = S2.get(a);

            if ( s1.getPosition() != s2.getPosition()
                    || s1.getCaption().equals(s2.getCaption()) == false )
                return true;
        }

        return false;
    }

    private void setCursor(Cursor cursor) {

        ArrayList<SectionItem> Sections = new ArrayList<>();

        currentSectionIndex = 0;

        if ( cursor != null
                && !cursor.isClosed() ) {

            String caption = null;
            int col_idx = cursor.getColumnIndex("section");

            if ( cursor.moveToFirst() )
                do {

                    if ( caption == null
                            || cursor.getString(col_idx).equals(caption) == false ) {

                        caption = cursor.getString(col_idx);
                        Sections.add(new SectionItem(Sections.size() + cursor.getPosition(), caption));
                    }

                }while (cursor.moveToNext());

        }

        if ( this.Sections == null ) {

            this.Sections = Sections;

        } else {

            if ( SectionsDiff(this.Sections, Sections) ) {
                this.Sections.clear();
                this.Sections = Sections;
            }


        }

        this.cursor = cursor;
    }

    @Override
    public int getCount() {

        if ( cursor != null
                && cursor.isClosed() == false )
            return cursor.getCount() + Sections.size();

        return 0;
    }

    @Override
    public Object getItem(int position) {

        if ( Sections.size() == 0 ) {
            cursor.moveToPosition(position);
            return cursor;
        }

        SectionItem section = Sections.get(currentSectionIndex);

        if ( section.getPosition() == position ) {
            return section;
        }

        if ( currentSectionIndex == 0
             && position < section.getPosition() ) {

            cursor.moveToPosition(position);
            return cursor;
        }

        if ( position > section.getPosition()
                && currentSectionIndex == Sections.size()-1 ) {

            cursor.moveToPosition(position-currentSectionIndex-1);
            return cursor;
        }

        int start = currentSectionIndex+1;
        int size = Sections.size();

        for(int n=0;n<2;n++) {

            for(int a = start;a<size;a++) {

                section = Sections.get(a);

                if ( section.getPosition() == position ) {
                    return section;
                }

                if ( position > section.getPosition()
                        && ( a == size-1
                        || position < Sections.get(a+1).getPosition() ) ) {

                    currentSectionIndex = a;

                    cursor.moveToPosition(position-a-1);
                    return cursor;
                }
            }

            start = 0;
        }


        return null;
    }

    public int getSectionIndexAtPosition(int position) {

        if ( Sections.size() == 0 ) {
            return -1;
        }

        SectionItem section = Sections.get(currentSectionIndex);

        if ( section.getPosition() == position ) {
            return currentSectionIndex;
        }

        int size = Sections.size();

        for(int a = 0;a<size;a++) {

            section = Sections.get(a);

            if ( section.getPosition() == position ) {
                return a;
            }

            if ( position > section.getPosition()
                    && ( a == size-1
                    || position < Sections.get(a+1).getPosition() ) ) {

                return a;
            }
        }

        return -1;

    }

    public SectionItem getSectionAtIndex(int idx) {

        if ( idx < 0
                || idx >= Sections.size() )
            return null;

        return Sections.get(idx);
    }

    public SectionItem getSectionAtPosition(int position) {

       return getSectionAtIndex(getSectionIndexAtPosition(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof SectionItem ) {
            return TYPE_SECTION;
        }

        return TYPE_CHANNEL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Object obj = getItem(position);

        if ( obj instanceof SectionItem ) {

            if ( ((SectionItem)obj).view == null ) {
                ((SectionItem)obj).view = new SectionLayout(context);
                ((SectionItem)obj).view.setCaption(((SectionItem)obj).getCaption());
            }

            convertView = ((SectionItem)obj).view;

        } else if ( obj instanceof Cursor ) {

            if ( convertView == null || !(convertView instanceof ChannelLayout) ) {
                convertView = new ChannelLayout(context, parent instanceof ChannelListView ? (ChannelListView)parent : null);
            }

            Channel channel = new Channel();
            channel.AssignCursorData((Cursor)obj);
            setData((ChannelLayout) convertView, channel);
        }


        return convertView;
    }


    public void setData(ChannelLayout channelLayout, Channel channel) {
        channelLayout.setChannelData(channel);
    }

    public Cursor getCursor() {
        return this.cursor;
    }

    public void changeCursor(Cursor cursor) {

        if ( this.cursor != null ) {
            this.cursor.close();
        }

        setCursor(cursor);
        notifyDataSetInvalidated();

    }
}
