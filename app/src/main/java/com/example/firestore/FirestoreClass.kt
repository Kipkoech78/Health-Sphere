package com.example.firestore
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.healthsphere.RegisterActivity
import com.example.healthsphere.SignIn
import com.example.models.Appointment
import com.example.models.CartItem
import com.example.models.Doctor
import com.example.models.DoctorBooking
import com.example.models.Medicine
import com.example.models.Order
import com.example.models.User
import com.example.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()
    fun registerUser(activity: RegisterActivity, userInfo: User){
        // the "users" is collection name .If collection already exist then it will not create the same users
        mFirestore.collection(Constants.USERS)
            // document id for the users  field
            .document(userInfo.id)
            //here the user info are field and setOption are set to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                //here we call the activity of base activity for transferring the result to it
                activity.userRegistrationSucceess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName, "error while registering user", e
                )
            }
    }
    fun getCurrentUserID(): String {
        // an instance of current user using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser
        //variable to assign current user if it is not null or else it will be blank.
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
    fun getUserDetails(activity: Activity){
        //here we pass the collection name from which we want the data
        mFirestore.collection(Constants.USERS)
            //the document id to get the Fields of users
            .document(getCurrentUserID()).get().addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                //here we have received the document snapshot which is converted into user Data model object
                val user = document.toObject(User::class.java)!!


                val sharedPreferences = activity.getSharedPreferences(
                    Constants.HEALTHAPP_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                //key : logged_in_username
                //value: Username
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.userName}",)
                editor.putString(Constants.LOGGED_IN_USEREMAIL, "${user.email}")
                editor.putString(Constants.ADMIN, "${user.role}")
                editor.apply()

                when( activity){
                    is SignIn -> {
                        //call a base activity function to transfer result to it
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
            .addOnFailureListener {e ->
                when(activity){
                    is SignIn ->{
                        activity.hideProgressDialog()
                    }
                }
            }
    }
    // Method to check if a cart item already exists
    fun checkCartItemExists(username: String, product: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        mFirestore.collection( "cart")
            .whereEqualTo("username", username)
            .whereEqualTo("product", product)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Check if any document exists
                val exists = !querySnapshot.isEmpty
                onSuccess(exists)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun addDrug(
        drugName: String,
        price: String,
        category: String,
        description: String,
        sideEffect: String,
        drugImg: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val drugItem = Medicine(
            drugName = drugName,
            price = price,
            category =category,
            sideEffect = sideEffect,
            description = description,
            drugImg = drugImg
        )

        mFirestore.collection(Constants.DRUG)  // Make sure Constants.DRUG points to the correct collection
            .add(drugItem)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.i("FirestoreClass", "Error adding drug to db", e)
                onFailure(e)
            }
    }
    private val db = FirebaseFirestore.getInstance()
    fun getDoctorBookingsForUser(username: String, onSuccess: (List<DoctorBooking>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("appointments")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                val bookings = mutableListOf<DoctorBooking>()
                for (document in result) {
                    val booking = document.toObject(DoctorBooking::class.java)
                    bookings.add(booking)
                }
                onSuccess(bookings)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun updateAppointmentStatus(appointmentId: String, newStatus: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val appointmentRef = db.collection("appointments").document(appointmentId)
        appointmentRef.update("status", newStatus)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun deleteAppointment(appointmentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("appointments").document(appointmentId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    fun getAppointmentsForDoctor(doctorName: String, onSuccess: (List<Appointment>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("appointments")
            .whereEqualTo("doctorName", doctorName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val appointments = querySnapshot.toObjects(Appointment::class.java)
                onSuccess(appointments)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun addCartItem(cartItem: CartItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        mFirestore.collection( "cart")
            .add(cartItem)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error adding cart item", e)
                onFailure(e)
            }
    }
    fun getCartItemsByUser(username: String, onSuccess: (List<CartItem>) -> Unit, onFailure: (Exception) -> Unit) {
        mFirestore.collection( "cart")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                val cartItems = result.mapNotNull { document ->
                    document.toObject(CartItem::class.java)
                }
                onSuccess(cartItems)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error fetching cart items", e)
                onFailure(e)
            }
    }
    // FirestoreClass.kt
    fun clearCartItems(username: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        mFirestore.collection( "cart")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                val batch = mFirestore.batch()
                for (document in result.documents) {
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreClass", "Error clearing cart items", e)
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error getting cart items", e)
                onFailure(e)
            }
    }
    fun removeCartItem(username: String, product: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        mFirestore.collection( "cart")
            .whereEqualTo("username", username)
            .whereEqualTo("product", product)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // Assuming only one document is returned
                    val documentId = result.documents[0].id
                    mFirestore.collection( "cart")
                        .document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreClass", "Error removing cart item", e)
                            onFailure(e)
                        }
                } else {
                    // Handle case where item was not found
                    Log.w("FirestoreClass", "No matching cart item found")
                    onSuccess()  // Consider calling onFailure() or providing feedback to user
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error checking cart item for removal", e)
                onFailure(e)
            }
    }
    fun placeOrder(order: Order, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        mFirestore.collection("orders")
            .add(order)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error placing order", e)
                onFailure(e)
            }
    }
    fun getOrdersForUser(username: String, onSuccess: (List<Order>) -> Unit, onFailure: (Exception) -> Unit) {
        mFirestore.collection("orders")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                val orders = result.mapNotNull { document ->
                    document.toObject(Order::class.java)
                }
                onSuccess(orders)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun getUserOrders(username: String, onSuccess: (List<Order>) -> Unit, onFailure: (Exception) -> Unit) {
        mFirestore.collection("orders")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                val orders = result.mapNotNull { document ->
                    document.toObject(Order::class.java)
                }
                onSuccess(orders)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error fetching orders", e)
                onFailure(e)
            }
    }
    fun getCartItemsByUserId(userId: String, onSuccess: (List<CartItem>) -> Unit, onFailure: (Exception) -> Unit) {
        mFirestore.collection( "cart")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val cartItems = result.mapNotNull { document ->
                    document.toObject(CartItem::class.java)
                }
                onSuccess(cartItems)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error fetching cart items", e)
                onFailure(e)
            }
    }

}
