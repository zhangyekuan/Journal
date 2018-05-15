package database.JournalDbSchema;
public class JournalDbSchema {
    public static final class JournalTable{
        public static final String NAME = "journals";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String CONTENT = "content";
            public static final String YEAR = "year";
            public static final String MONTH = "month";
            public static final String DAY = "day";
            public static final String PATH = "path";
            public static final String LOCATION = "location";
        }
    }
}
