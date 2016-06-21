package john.com.moim.database;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String TITLE = "title";
        public static final String MOIM_TITLE = "moim_title";
        public static final String PLACE_TITLE = "place_title";

        public static final String CREATE_DATE = "create_date";
        public static final String REMOVE_DATE = "remove_date";
        public static final String ETC1 = "etc1";

        public static final String PEOPLE = "people";
        public static final String UNIT = "unit";
        public static final String TOTAL = "total";
        public static final String REMAINDER = "remainder";
        public static final String RESULT = "result";
        public static final String NAME = "name";
        public static final String FREQUENT = "frequent";

        public static final String _TABLENAME = "moim";
        public static final String _TABLENAME2 = "place";
        public static final String _TABLENAME3 = "place_result";
        public static final String _TABLENAME4 = "address";

        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + _ID + " integer primary key autoincrement, "
                        + TITLE + " text not null unique, "
                        + CREATE_DATE + " text, "
                        + REMOVE_DATE + " text, "
                        + ETC1 + " text );";

        public static final String _CREATE2 =
                "create table " + _TABLENAME2 + "("
                        + _ID + " integer primary key autoincrement, "
                        + MOIM_TITLE + " text not null, "
                        + TITLE + " text not null, "
                        + CREATE_DATE + " text, "
                        + REMOVE_DATE + " text, "
                        + ETC1 + " text );";

        public static final String _CREATE3 =
                "create table " + _TABLENAME3 + "("
                        + _ID + " integer primary key autoincrement, "
                        + MOIM_TITLE + " text not null, "
                        + PLACE_TITLE + " text not null, "
                        + CREATE_DATE + " text, "
                        + REMOVE_DATE + " text, "
                        + PEOPLE + " text, "
                        + UNIT + " text, "
                        + TOTAL + " text, "
                        + REMAINDER + " text, "
                        + RESULT + " text, "
                        + ETC1 + " text );";

        public static final String _CREATE4 =
                "create table " + _TABLENAME4 + "("
                        + _ID + " integer primary key autoincrement, "
                        + NAME + " text not null, "
                        + FREQUENT + " text, "
                        + CREATE_DATE + " text, "
                        + ETC1 + " text );";
    }
}
