package de.takeweiland.sqlgenerator

import de.takeweiland.sqlast.*
import de.takeweiland.sqlast.visitor.DataTypeVisitor
import de.takeweiland.sqlast.visitor.ExpressionVisitor

class ExprVisitorImpl(private val t: Appendable) : ExpressionVisitor, DataTypeVisitor {

    override fun visitBoolean(expr: Value.Boolean) {
        t.append(if (expr.value) "TRUE" else "FALSE")
    }

    override fun visitLong(expr: Value.Long) {
        t.append(expr.value.toString())
    }

    override fun visitDouble(expr: Value.Double) {
        t.append(expr.value.toString())
    }

    override fun visitBetween(expr: Expr.Between) {
        expr.expr.accept(this)
        t.append(" BETWEEN ")
        expr.low.accept(this)
        t.append(" AND ")
        expr.high.accept(this)
    }

    override fun visitBinaryOp(expr: Expr.BinaryOp) {
        expr.left.accept(this)
        t.append(' ')
        t.append(
            when (expr.op) {
                BinaryOperator.PLUS -> "+"
                BinaryOperator.MINUS -> "-"
                BinaryOperator.MULTIPLY -> "*"
                BinaryOperator.DIVIDE -> "/"
                BinaryOperator.MODULUS -> "%"
                BinaryOperator.GT -> ">"
                BinaryOperator.LT -> "<"
                BinaryOperator.GTE -> ">="
                BinaryOperator.LTE -> "<="
                BinaryOperator.EQ -> "=="
                BinaryOperator.NEQ -> "!="
                BinaryOperator.AND -> "AND"
                BinaryOperator.OR -> "OR"
                BinaryOperator.LIKE -> "LIKE"
                BinaryOperator.NOT_LIKE -> "NOT LIKE"
            }
        )
        t.append(' ')
        expr.right.accept(this)
    }

    override fun visitCase(expr: Expr.Case) {
        t.append("CASE")
        expr.subject?.let {
            t.append(' ')
            it.accept(this)
        }
        for (branch in expr.branches) {
            t.append(" WHEN ")
            branch.condition.accept(this)
            t.append(" THEN ")
            branch.result.accept(this)
        }
        expr.elseExpr?.let {
            t.append(' ')
            it.accept(this)
        }
        t.append(" END")
    }

    override fun visitCast(expr: Expr.Cast) {
        t.append("CAST(")
        expr.accept(this)
        t.append(" AS ")
        expr.dataType.accept(this)
        t.append(')')
    }

    override fun visitCollate(expr: Expr.Collate) {
        TODO("Not yet implemented")
    }

    override fun visitCompoundIdentifier(expr: Expr.CompoundIdentifier) {
        TODO("Not yet implemented")
    }

    override fun visitExists(expr: Expr.Exists) {
        TODO("Not yet implemented")
    }

    override fun visitExtract(expr: Expr.Extract) {
        TODO("Not yet implemented")
    }

    override fun visitFunction(expr: Expr.Function) {
        TODO("Not yet implemented")
    }

    override fun visitIdentifier(expr: Expr.Identifier) {
        TODO("Not yet implemented")
    }

    override fun visitInList(expr: Expr.InList) {
        if (expr.negated) {
            t.append("NOT ")
        }
        t.append("IN (")
        var first = true
        for (item in expr.list) {
            if (!first) {
                t.append(',')
                first = false
            }
            item.accept(this)
        }
        t.append(')')
    }

    override fun visitInSubquery(expr: Expr.InSubquery) {
        TODO("Not yet implemented")
    }

    override fun visitInt(expr: Value.Int) {
        t.append(expr.value.toString())
    }

    override fun visitInterval(expr: Value.Interval) {
        TODO("Not yet implemented")
    }

    override fun visitIsNotNull(expr: Expr.IsNotNull) {
        expr.expr.accept(this)
        t.append(" IS NOT NULL")
    }

    override fun visitIsNull(expr: Expr.IsNull) {
        expr.expr.accept(this)
        t.append(" IS NULL")
    }

    override fun visitNested(expr: Expr.Nested) {
        t.append('(')
        expr.expr.accept(this)
        t.append(')')
    }

    override fun visitNull() {
        t.append("NULL")
    }

    override fun visitNumber(expr: Value.Number) {
        TODO("Not yet implemented")
    }

    override fun visitWildcard() {
        t.append('*')
    }

    override fun visitQualifiedWildcard(expr: Expr.QualifiedWildcard) {
        TODO("Not yet implemented")
    }

    override fun visitString(expr: Value.String) {
        addStringLiteral(expr.value)
    }

    override fun visitNString(expr: Value.NString) {
        t.append('N')
        addStringLiteral(expr.value)
    }

    override fun visitHexString(expr: Value.HexString) {
        t.append('X')
        addStringLiteral(expr.value)
    }

    private fun addStringLiteral(value: String) {
        t.append('\'')
        t.append(value.replace("'", "''"))
        t.append('\'')
    }

    override fun visitSubquery(expr: Expr.Subquery) {
        TODO("Not yet implemented")
    }

    override fun visitTime(expr: Value.Time) {
        t.append("TIME '")
        appendTime(expr.value)
        t.append('\'')
    }

    override fun visitDate(expr: Value.Date) {
        t.append("DATE '")
        appendDate(expr.value)
        t.append('\'')
    }

    override fun visitTimestamp(expr: Value.Timestamp) {
        t.append("TIMESTAMP '")
        appendDate(expr.date)
        t.append(' ')
        appendTime(expr.time)
        t.append('\'')
    }

    override fun visitTimestampWithTimezone(expr: Value.TimestampWithTimezone) {
        t.append("TIMESTAMP WITH TIMEZONE '")
        appendDate(expr.date)
        t.append(' ')
        appendTime(expr.time)
        t.append(expr.timeZone)
        t.append('\'')
    }

    private fun appendTime(time: LocalTime) {
        t.append(time.hour.toString())
        t.append(':')
        t.append(time.minute.toString())
        t.append(':')
        t.append(time.hour.toString())
        val nano = time.nano
        if (nano != 0) {
            t.append('.')
            t.append(nano.toString().padStart(9, '0'))
        }
    }

    private fun appendDate(date: LocalDate) {
        t.append(date.year.toString())
        t.append('-')
        t.append(date.month.toString())
        t.append('-')
        t.append(date.day.toString())
    }

    override fun visitUnaryOp(expr: Expr.UnaryOp) {
        t.append(
            when (expr.op) {
                UnaryOperator.MINUS -> "- "
                UnaryOperator.PLUS -> "+ "
                UnaryOperator.NOT -> "NOT "
            }
        )
        expr.expr.accept(this)
    }

    // DataType
    override fun visitBoolean() {
        t.append("BOOLEAN")
    }

    override fun visitSmallInt() {
        t.append("SMALLINT")
    }

    override fun visitInt() {
        t.append("INT")
    }

    override fun visitBigInt() {
        t.append("BIGINT")
    }

    override fun visitDouble() {
        t.append("DOUBLE")
    }

    override fun visitReal() {
        t.append("REAL")
    }

    override fun visitDecimal(type: DataType.Decimal) {
        visitNumericType("DECIMAL", type.precision, type.scale)
    }

    override fun visitNumeric(type: DataType.Numeric) {
        visitNumericType("NUMERIC", type.precision, type.scale)
    }

    override fun visitFloat(type: DataType.Float) {
        visitNumericType("FLOAT", type.precision, null)
    }

    private fun visitNumericType(name: String, precision: Long?, scale: Long?) {
        t.append(name)
        if (precision != null || scale != null) {
            t.append('(')
        }
        if (precision != null) {
            t.append(precision.toString())
        }
        if (scale != null) {
            if (precision != null) {
                t.append(',')
            }
            t.append(scale.toString())
        }
        if (precision != null || scale != null) {
            t.append(')')
        }
    }

    override fun visitChar(type: DataType.Char) {
        t.append("CHAR")
    }

    override fun visitVarchar(type: DataType.Varchar) {
        t.append("VARCHAR")
    }

    override fun visitClob(type: DataType.Clob) {
        t.append("CLOB")
    }

    override fun visitBinary(type: DataType.Binary) {
        t.append("BINARY")
    }

    override fun visitVarbinary(type: DataType.Varbinary) {
        t.append("VARBINARY")
    }

    override fun visitBlob(type: DataType.Blob) {
        t.append("BLOB")
    }

    override fun visitDate() {
        t.append("DATE")
    }

    override fun visitInterval() {
        t.append("INTERVAL")
    }

    override fun visitText() {
        t.append("TEXT")
    }

    override fun visitTime() {
        t.append("TIME")
    }

    override fun visitTimestamp() {
        t.append("TIMESTAMP")
    }

    override fun visitTimestampWithTimezone() {
        t.append("TIMESTAMP WITH TIMEZONE")
    }

    override fun visitUUID() {
        t.append("UUID")
    }

    override fun visitArray(type: DataType.Array) {
        type.elementType.accept(this)
        t.append("[]")
    }

    override fun visitCustom(type: DataType.Custom) {
        t.append(type.identifier)
    }
}
