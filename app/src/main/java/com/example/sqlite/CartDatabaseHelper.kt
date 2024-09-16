package com.example.sqlite


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

// Define CartItem data model
data class CartItem(
    val id: Int = 0,
    val drugName: String,
    val drugDescription: String,
    val price: String,
    val image: String,
    val userId: String
)

class CartDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cart.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "medicine_cart"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DRUG_NAME = "drug_name"
        private const val COLUMN_DRUG_DESCRIPTION = "drug_description"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CART_TABLE = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_DRUG_NAME TEXT, "
                + "$COLUMN_DRUG_DESCRIPTION TEXT, "
                + "$COLUMN_PRICE TEXT, "
                + "$COLUMN_IMAGE TEXT, "
                + "$COLUMN_USER_ID TEXT)")
        db?.execSQL(CREATE_CART_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert CartItem
    fun insertCartItem(cartItem: CartItem): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_DRUG_NAME, cartItem.drugName)
            put(COLUMN_DRUG_DESCRIPTION, cartItem.drugDescription)
            put(COLUMN_PRICE, cartItem.price)
            put(COLUMN_IMAGE, cartItem.image)
            put(COLUMN_USER_ID, cartItem.userId)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }

    // Retrieve all CartItems
    fun getAllCartItems(): List<CartItem> {
        val cartItems = mutableListOf<CartItem>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val drugName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRUG_NAME))
                val drugDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DRUG_DESCRIPTION))
                val price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
                val image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
                val userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
                cartItems.add(CartItem(id, drugName, drugDescription, price, image, userId))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return cartItems
    }

    // Update CartItem
    fun updateCartItem(cartItem: CartItem): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_DRUG_NAME, cartItem.drugName)
            put(COLUMN_DRUG_DESCRIPTION, cartItem.drugDescription)
            put(COLUMN_PRICE, cartItem.price)
            put(COLUMN_IMAGE, cartItem.image)
        }
        return db.update(TABLE_NAME, contentValues, "$COLUMN_ID=?", arrayOf(cartItem.id.toString()))
    }

    // Delete CartItem
    fun deleteCartItem(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    // Clear the entire cart
    fun clearCart(): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, null, null)
    }
}
