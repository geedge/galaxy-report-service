package com.mesa.reportservice.util;

import com.mesa.reportservice.bean.DBTypeEnum;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;

/**
 * Created by wk1 on 2020/1/8.
 */
public class FormatSqlUtil {

    public static String formatSql(String sql) {

        try {
            sql = SQLDateFunctionUtil.generateDateFunction(sql, DBTypeEnum.CLICKHOUSE.getValue());
            // Statement statement = CCJSqlParserUtil.parse(sql);
            sql = generateStandard(sql);
        } catch (Exception e) {
            Logs.error("sqlParser error: " + e.toString());
            return "";
        }
        return sql;
    }


    private static SelectBody generateSubqueryAlias(String sql) {

        SelectBody resultSelectBody = null;
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);

            if (statement instanceof Select) {

                Select selectStatement = (Select) statement;

                SelectBody body = selectStatement.getSelectBody();

                if (body instanceof PlainSelect) {

                    PlainSelect plainSelect = (PlainSelect) body;

                    //子查询处理
                    Expression where = plainSelect.getWhere();
                    if (where != null) {
                        setInSubqueryAlias(where);
                    }

                    resultSelectBody = plainSelect;

                } else if (body instanceof SetOperationList) {//连接查询
                    SetOperationList setOperationList = (SetOperationList) body;
                    List<SelectBody> selectsList = setOperationList.getSelects();
                    for (int i = 0; i < selectsList.size(); i++) {
                        if (selectsList.get(i) instanceof PlainSelect) {
                            SelectBody selectBody = generateSubqueryAlias(selectsList.get(i).toString());
                            setOperationList.getSelects().set(i, selectBody);
                        }

                    }

                    resultSelectBody = setOperationList;

                }
            }

        } catch (JSQLParserException e) {
            Logs.error("sqlParser Alias error: " + e.toString());

        }

        return resultSelectBody;

    }


    private static String generateStandard(String sql) {
        //sql = generateCount(sql);
        SelectBody body = generateSubqueryAlias(sql);
        if (StringUtil.isNotEmpty(body)) {
            sql = body.toString();
        }
        return sql;
    }


    /**
     * 子查询别名处理
     *
     * @param where
     */
    private static void setInSubqueryAlias(Expression where) {
        if (where instanceof InExpression) {//where in 查询处理
            InExpression whereIn = (InExpression) where;
            ItemsList rightItemsList = whereIn.getRightItemsList();
            if (rightItemsList instanceof SubSelect) {
                SubSelect sub = (SubSelect) rightItemsList;
                SelectBody selectBody = sub.getSelectBody();
                PlainSelect plainSelect = (PlainSelect) selectBody;
                Table table = (Table) plainSelect.getFromItem();
                if (StringUtil.isEmpty(table.getAlias())) {
                    Alias alias = new Alias(table.getName());
                    plainSelect.getFromItem().setAlias(alias);
                }
                if (StringUtil.isNotEmpty(plainSelect.getWhere())) {
                    setInSubqueryAlias(plainSelect.getWhere());
                }

            }

        } else if (where instanceof AndExpression) {
            AndExpression whereAnd = (AndExpression) where;
            Expression leftExpression = whereAnd.getLeftExpression();
            setInSubqueryAlias(leftExpression);
            Expression rightExpression = whereAnd.getRightExpression();
            setInSubqueryAlias(rightExpression);
        } else if (where instanceof OrExpression) {
            OrExpression whereOr = (OrExpression) where;
            Expression leftExpression = whereOr.getLeftExpression();
            setInSubqueryAlias(leftExpression);
            Expression rightExpression = whereOr.getRightExpression();
            setInSubqueryAlias(rightExpression);
        }
    }


}