import de.takeweiland.sqlast.*
import de.takeweiland.sqlgenerator.ExprVisitorImpl

fun main() {
    val query = Query(
        body = Select(
            items = listOf(Select.Item(Value.String("Hello"))),
            from = listOf(TableFactor.Table(ObjectName("dual")))
        )
    )

    val sql = buildString {
        val visitor = ExprVisitorImpl(this)
        Value.String("Hello World").accept(visitor)
    }

    println(sql)
}

