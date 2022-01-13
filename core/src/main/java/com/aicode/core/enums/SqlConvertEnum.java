package com.aicode.core.enums;

//(SET|set)\s+(NAMES|names)\s+\w+;
//(SET|set)\s+(FOREIGN_KEY_CHECKS|foreign_key_checks)\s+=\s+0;
//(DEFAULT|default)\s+(null|NULL)
//(COLLATE|collate)\s+\w+
//(AUTO_INCREMENT|auto_increment)
//(ENGINE|engine)=\w+
//(DEFAULT|default)\s+(CHARSET|charset)=\w+
//(COLLATE|collate)=\w+
///\*[a-zA-Z!\[\]*_\s/:\d.]+(\*/)
//(--\s+[-]+)|(--\s+[a-zA-Z\s_]+)
//(AUTO_INCREMENT|auto_increment)\s+=\s+\d+
//(SET FOREIGN_KEY_CHECKS|set foreign_key_checks) = 1;
public enum SqlConvertEnum {
    DROP("(DROP TABLE IF EXISTS|drop table if exists)\\s+[`\\w]+;{0,1}"),
    CREATE_DATABASE("\\s*(create|CREATE)\\s+(database|DATABASE)\\s+\\w+;{0,1}"),
    USE("^\\s*(use|USE)\\s+\\w+;"),
    SET_NAMES("(SET|set)\\s+(NAMES|names)\\s+\\w+;"),
    SET_FOREIGN_KEY_CHECKS_0("(SET|set)\\s+(FOREIGN_KEY_CHECKS|foreign_key_checks)\\s+=\\s+0;"),
    DEFAULT_NULL("(DEFAULT|default)\\s+(null|NULL)"),
    COLLATE("(COLLATE|collate)[\\s=]+\\w+"),
    AUTO_INCREMENT("(AUTO_INCREMENT|auto_increment)[\\s=\\d]+"),
    ENGINE("(ENGINE|engine)=\\w+"),
    DEFAULT_CHARSET("(DEFAULT|default)\\s+(CHARSET|charset)=\\w+"),
    COMMENT("/\\*[a-zA-Z!\\[\\]*_\\s/:\\d.]+(\\*/)"),
    SQL_COMMENT("(--\\s+[-]+)|(--\\s+[a-zA-Z\\s_]+)"),
    SET_FOREIGN_KEY_CHECKS_1("(SET FOREIGN_KEY_CHECKS|set foreign_key_checks) = 1;"),
    ;
    public String regex;

    SqlConvertEnum(String regex) {
        this.regex = regex;
    }

    /**
     * 根据正则替换为空
     *
     * @param template sql 模板
     * @return sql
     */
    public String replaceAll(String template) {
        return template.replaceAll(this.regex, "");
    }

}
