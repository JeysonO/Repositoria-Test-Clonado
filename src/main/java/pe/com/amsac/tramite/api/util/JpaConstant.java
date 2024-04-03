package pe.com.amsac.tramite.api.util;

public class JpaConstant {
    public static final String ALIAS_ENTITY = "p";
    public static final String CLAUSE_SELECT = "select";
    public static final String CLAUSE_FROM = "from";
    public static final String CLAUSE_WHERE = "where";
    public static final String CLAUSE_GROUP_BY = "group by";
    public static final String CLAUSE_ORDER_BY = "order by";
    public static final String CONDITION_OR = "or";
    public static final String CONDITION_AND = "and";
    public static final String OPERATOR_EQUALS = "=";
    public static final String OPERATOR_NOT_EQUAL = "<>";
    public static final String OPERATOR_LESS_THAN = "<";
    public static final String OPERATOR_LESS_THAN_EQUAL_TO = "<=";
    public static final String OPERATOR_GREATER_THAN = ">";
    public static final String OPERATOR_GREATER_THAN_EQUAL_TO = ">=";
    public static final String OPERATOR_LIKE = "like";
    public static final String OPERATOR_IS_NULL = "is null";
    public static final String OPERATOR_IS_NOT_NULL = "is not null";
    public static final String OPERATOR_BETWEEN = "between";
    public static final String OPERATOR_BETWEEN_AND = "and";
    public static final String OPERATOR_IN = "in";
    public static final String OPERATOR_NOT_IN = "not in";
    public static final String ORDER_ASC = "asc";
    public static final String ORDER_DESC = "desc";
    public static final String ENTITY_ALIAS_SEPARATOR = "@";
    public static final String ENTITY_PROPERTY_SEPARATOR = ".";
    public static final String PREFIX_PARAMETER = ":";
    public static final String PREFIX_SUFIX_LIKE = "%";
    public static final String LIST_START = "(";
    public static final String LIST_END = ")";
    public static final String LIST_SEPARATOR = ",";

    protected JpaConstant() {
        throw new UnsupportedOperationException();
    }
}
