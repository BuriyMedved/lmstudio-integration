package org.buriy.medved.backend.entity

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.type.SqlTypes
import org.hibernate.usertype.UserType
import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

class CustomFloatArrayType: UserType<FloatArray> {
    private val columnType = Types.ARRAY

    override fun getSqlType(): Int {
        return columnType
    }

    override fun returnedClass(): Class<FloatArray> {
        return FloatArray::class.java
    }

    override fun equals(x: FloatArray, y: FloatArray): Boolean {
        return x.contentEquals(y)
    }

    override fun hashCode(x: FloatArray): Int {
        return x.contentHashCode()
    }

    override fun nullSafeGet(
        rs: ResultSet,
        position: Int,
        session: SharedSessionContractImplementor?,
        owner: Any?
    ): FloatArray {
        val array = rs.getArray(position)
        return array.array as FloatArray
    }

    override fun nullSafeSet(
        st: PreparedStatement,
        value: FloatArray?,
        index: Int,
        session: SharedSessionContractImplementor
    ) {
        if(value == null) {
            st.setNull(index, columnType)
        }
        else{
            val elements = value.toTypedArray()
            val array = st.connection.createArrayOf("real", elements)
            st.setArray(index, array)
        }
    }

    override fun deepCopy(value: FloatArray?): FloatArray? {
        return value
    }

    override fun isMutable(): Boolean {
        return true
    }

    override fun disassemble(value: FloatArray?): Serializable? {
        return this.deepCopy(value)
    }

    override fun assemble(cached: Serializable?, owner: Any?): FloatArray? {
        return this.deepCopy(cached as FloatArray?)
    }
}