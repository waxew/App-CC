// [AS-TEAM-DOCUMENTED]
// فایل Commerce.kt هسته‌ی فروشگاهی CACTUS Collection است.
// تمام منطق این فایل Local-first است؛ یعنی بدون سرور نیز کاتالوگ، سبد، طراحی، سفارش و باشگاه کار می‌کنند.
// بعداً می‌توان همین API داخلی را به Repository ابری متصل کرد بدون اینکه UI اصلی دوباره نوشته شود.
package com.asteam.cactuscollection

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asteam.cactuscollection.ui.theme.CactusBackground
import com.asteam.cactuscollection.ui.theme.CactusBlue
import com.asteam.cactuscollection.ui.theme.CactusLavender
import com.asteam.cactuscollection.ui.theme.CactusMint
import com.asteam.cactuscollection.ui.theme.CactusMuted
import com.asteam.cactuscollection.ui.theme.CactusPeach
import com.asteam.cactuscollection.ui.theme.CactusPink
import com.asteam.cactuscollection.ui.theme.CactusPurple
import com.asteam.cactuscollection.ui.theme.CactusPurpleDark
import com.asteam.cactuscollection.ui.theme.CactusText
import com.asteam.cactuscollection.ui.theme.CactusYellow
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.max

// مدل ثابت محصول فروشگاه؛ در نسخه‌ی ابری می‌تواند از API دریافت شود.
internal data class StoreProduct(
    val id: String,
    val title: String,
    val category: String,
    val price: Long,
    val description: String,
    @DrawableRes val icon: Int
)

// هر ردیف سبد خرید snapshot قیمت و شخصی‌سازی را نگه می‌دارد.
internal data class CartLine(
    val id: String,
    val productId: String,
    val title: String,
    val unitPrice: Long,
    val quantity: Int,
    val variant: String = "",
    val note: String = ""
)

// پروژه‌ی طراحی کاربر؛ فایل تصویر با URI محلی ذخیره می‌شود.
internal data class SavedDesign(
    val id: String,
    val title: String,
    val garment: String,
    val garmentColor: String,
    val printLocation: String,
    val widthCm: Int,
    val heightCm: Int,
    val customText: String,
    val artworkUri: String,
    val estimatedPrice: Long,
    val createdAt: Long
)

// مدل سفارش ثبت‌شده روی گوشی.
internal data class StoreOrder(
    val id: String,
    val createdAt: Long,
    val total: Long,
    val status: String,
    val customerName: String,
    val phone: String,
    val city: String,
    val address: String,
    val postalCode: String,
    val notes: String,
    val items: List<CartLine>
)

// CactusStore لایه‌ی ذخیره‌سازی Local-first برنامه است.
// SharedPreferences برای V2 کافی است و ساختار آن طوری نوشته شده که بعداً به Room/Backend منتقل شود.
internal class CactusStore(context: Context) {
    private val prefs = context.getSharedPreferences("cactus_commerce_v2", Context.MODE_PRIVATE)

    // کاتالوگ واقعی قابل خرید داخل نسخه‌ی آفلاین.
    val products: List<StoreProduct> = listOf(
        StoreProduct("tee-white", "تیشرت سفید Premium", "تیشرت", 480_000, "پنبه‌ای، مناسب چاپ جلو و پشت", R.drawable.ic_tshirt),
        StoreProduct("tee-black", "تیشرت مشکی Oversize", "تیشرت", 540_000, "فیت اورسایز و مناسب چاپ DTF", R.drawable.ic_tshirt),
        StoreProduct("tee-heavy", "تیشرت Heavy Cotton", "تیشرت", 620_000, "پارچه ضخیم برای کالکشن حرفه‌ای", R.drawable.ic_tshirt),
        StoreProduct("hoodie-black", "هودی مشکی CACTUS", "هودی", 980_000, "دورس با چاپ سینه، پشت و آستین", R.drawable.ic_hoodie),
        StoreProduct("hoodie-lilac", "هودی یاسی Premium", "هودی", 1_050_000, "هودی یاسی با چاپ سفارشی", R.drawable.ic_hoodie),
        StoreProduct("pants-jogger", "شلوار اسلش چاپ‌پذیر", "شلوار", 790_000, "قابل چاپ روی ران و پاچه", R.drawable.ic_pants),
        StoreProduct("pants-baggy", "شلوار بگ Streetwear", "شلوار", 860_000, "فیت بگ برای طرح‌های خیابانی", R.drawable.ic_pants),
        StoreProduct("design-minimal", "پک طرح مینیمال", "طرح‌های آماده", 120_000, "طرح آماده برای شخصی‌سازی", R.drawable.ic_palette),
        StoreProduct("design-persian", "تایپوگرافی فارسی", "طرح‌های آماده", 140_000, "مجموعه طرح‌های فارسی", R.drawable.ic_palette),
        StoreProduct("design-street", "پک Streetwear", "طرح‌های آماده", 160_000, "مجموعه گرافیک سبک خیابانی", R.drawable.ic_palette)
    )

    // SnapshotStateList باعث می‌شود UI با تغییر داده‌ها خودکار به‌روزرسانی شود.
    val cart = mutableStateListOf<CartLine>()
    val savedDesigns = mutableStateListOf<SavedDesign>()
    val orders = mutableStateListOf<StoreOrder>()

    // امتیاز باشگاه از سفارش‌های ثبت‌شده تولید می‌شود.
    var loyaltyPoints by mutableStateOf(prefs.getInt(KEY_POINTS, 0))
        private set

    init {
        // بازیابی وضعیت قبلی در زمان شروع برنامه.
        loadCart()
        loadDesigns()
        loadOrders()
    }

    val cartCount: Int get() = cart.sumOf { it.quantity }
    val cartTotal: Long get() = cart.sumOf { it.unitPrice * it.quantity }

    // افزودن محصول عادی؛ اگر همان محصول وجود داشته باشد فقط تعداد زیاد می‌شود.
    fun addProduct(product: StoreProduct) {
        val index = cart.indexOfFirst { it.productId == product.id && it.variant == "استاندارد" }
        if (index >= 0) {
            val old = cart[index]
            cart[index] = old.copy(quantity = old.quantity + 1)
        } else {
            cart += CartLine(
                id = UUID.randomUUID().toString(),
                productId = product.id,
                title = product.title,
                unitPrice = product.price,
                quantity = 1,
                variant = "استاندارد"
            )
        }
        saveCart()
    }

    // افزودن آیتم سفارشی برای استودیو طراحی و سفارش عمده.
    fun addCustomLine(title: String, unitPrice: Long, quantity: Int, variant: String, note: String, productId: String) {
        cart += CartLine(
            id = UUID.randomUUID().toString(),
            productId = productId,
            title = title,
            unitPrice = unitPrice.coerceAtLeast(0),
            quantity = quantity.coerceIn(1, 1000),
            variant = variant,
            note = note
        )
        saveCart()
    }

    // تغییر تعداد یک ردیف سبد؛ مقدار صفر یعنی حذف.
    fun changeQuantity(lineId: String, delta: Int) {
        val index = cart.indexOfFirst { it.id == lineId }
        if (index < 0) return
        val current = cart[index]
        val next = current.quantity + delta
        if (next <= 0) cart.removeAt(index) else cart[index] = current.copy(quantity = next.coerceAtMost(1000))
        saveCart()
    }

    fun removeFromCart(lineId: String) {
        cart.removeAll { it.id == lineId }
        saveCart()
    }

    fun clearCart() {
        cart.clear()
        saveCart()
    }

    // ذخیره‌ی پروژه طراحی و برگرداندن همان مدل برای افزودن سریع به سبد.
    fun saveDesign(
        title: String,
        garment: String,
        garmentColor: String,
        printLocation: String,
        widthCm: Int,
        heightCm: Int,
        customText: String,
        artworkUri: String
    ): SavedDesign {
        val safeWidth = widthCm.coerceIn(5, 60)
        val safeHeight = heightCm.coerceIn(5, 80)
        val design = SavedDesign(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { "طرح بدون نام" },
            garment = garment,
            garmentColor = garmentColor,
            printLocation = printLocation,
            widthCm = safeWidth,
            heightCm = safeHeight,
            customText = customText.trim(),
            artworkUri = artworkUri,
            estimatedPrice = estimateDesignPrice(garment, safeWidth, safeHeight, printLocation),
            createdAt = System.currentTimeMillis()
        )
        savedDesigns.add(0, design)
        saveDesigns()
        return design
    }

    fun deleteDesign(id: String) {
        savedDesigns.removeAll { it.id == id }
        saveDesigns()
    }

    fun addDesignToCart(design: SavedDesign) {
        addCustomLine(
            title = "طراحی اختصاصی: ${design.title}",
            unitPrice = design.estimatedPrice,
            quantity = 1,
            variant = "${design.garment} / ${design.garmentColor} / ${design.printLocation}",
            note = "${design.widthCm}×${design.heightCm} cm | متن: ${design.customText.ifBlank { "ندارد" }} | ${if (design.artworkUri.isBlank()) "بدون فایل تصویر" else "فایل طرح انتخاب شده"}",
            productId = "design:${design.id}"
        )
    }

    // ایجاد سفارش نهایی از snapshot فعلی سبد.
    fun submitOrder(profile: UserProfile, name: String, phone: String, city: String, address: String, postalCode: String, notes: String): StoreOrder? {
        if (cart.isEmpty()) return null
        val now = System.currentTimeMillis()
        val order = StoreOrder(
            id = "CC-${now.toString().takeLast(8)}",
            createdAt = now,
            total = cartTotal,
            status = "ثبت شده",
            customerName = name.ifBlank { profile.fullName.ifBlank { "مشتری CACTUS" } },
            phone = phone.ifBlank { profile.phone },
            city = city.ifBlank { profile.city },
            address = address.trim(),
            postalCode = postalCode.trim(),
            notes = notes.trim(),
            items = cart.map { it.copy() }
        )
        orders.add(0, order)
        // حداقل پنج امتیاز؛ تقریباً یک امتیاز برای هر صد هزار تومان خرید.
        loyaltyPoints += max(5, (order.total / 100_000).toInt())
        prefs.edit().putInt(KEY_POINTS, loyaltyPoints).apply()
        saveOrders()
        clearCart()
        return order
    }

    // سفارش مجدد، snapshot سفارش قبلی را با شناسه‌های جدید به سبد بازمی‌گرداند.
    fun reorder(order: StoreOrder) {
        order.items.forEach { cart += it.copy(id = UUID.randomUUID().toString()) }
        saveCart()
    }

    // JSON خط سبد برای ذخیره‌سازی محلی.
    private fun cartJson(line: CartLine): JSONObject = JSONObject()
        .put("id", line.id)
        .put("productId", line.productId)
        .put("title", line.title)
        .put("unitPrice", line.unitPrice)
        .put("quantity", line.quantity)
        .put("variant", line.variant)
        .put("note", line.note)

    private fun saveCart() {
        val array = JSONArray()
        for (line in cart) array.put(cartJson(line))
        prefs.edit().putString(KEY_CART, array.toString()).apply()
    }

    private fun saveDesigns() {
        val array = JSONArray()
        for (design in savedDesigns) {
            array.put(JSONObject()
                .put("id", design.id)
                .put("title", design.title)
                .put("garment", design.garment)
                .put("garmentColor", design.garmentColor)
                .put("printLocation", design.printLocation)
                .put("widthCm", design.widthCm)
                .put("heightCm", design.heightCm)
                .put("customText", design.customText)
                .put("artworkUri", design.artworkUri)
                .put("estimatedPrice", design.estimatedPrice)
                .put("createdAt", design.createdAt))
        }
        prefs.edit().putString(KEY_DESIGNS, array.toString()).apply()
    }

    private fun saveOrders() {
        val array = JSONArray()
        for (order in orders) {
            val lines = JSONArray()
            for (line in order.items) lines.put(cartJson(line))
            array.put(JSONObject()
                .put("id", order.id)
                .put("createdAt", order.createdAt)
                .put("total", order.total)
                .put("status", order.status)
                .put("customerName", order.customerName)
                .put("phone", order.phone)
                .put("city", order.city)
                .put("address", order.address)
                .put("postalCode", order.postalCode)
                .put("notes", order.notes)
                .put("items", lines))
        }
        prefs.edit().putString(KEY_ORDERS, array.toString()).apply()
    }

    private fun loadCart() {
        val array = readArray(KEY_CART)
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            cart += CartLine(
                id = obj.optString("id", UUID.randomUUID().toString()),
                productId = obj.optString("productId", "legacy"),
                title = obj.optString("title", "محصول"),
                unitPrice = obj.optLong("unitPrice", 0),
                quantity = obj.optInt("quantity", 1).coerceAtLeast(1),
                variant = obj.optString("variant", ""),
                note = obj.optString("note", "")
            )
        }
    }

    private fun loadDesigns() {
        val array = readArray(KEY_DESIGNS)
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            savedDesigns += SavedDesign(
                id = obj.optString("id", UUID.randomUUID().toString()),
                title = obj.optString("title", "طرح"),
                garment = obj.optString("garment", "تیشرت"),
                garmentColor = obj.optString("garmentColor", "سفید"),
                printLocation = obj.optString("printLocation", "سینه"),
                widthCm = obj.optInt("widthCm", 20),
                heightCm = obj.optInt("heightCm", 25),
                customText = obj.optString("customText", ""),
                artworkUri = obj.optString("artworkUri", ""),
                estimatedPrice = obj.optLong("estimatedPrice", 0),
                createdAt = obj.optLong("createdAt", 0)
            )
        }
    }

    private fun loadOrders() {
        val array = readArray(KEY_ORDERS)
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val linesJson = obj.optJSONArray("items") ?: JSONArray()
            val lines = mutableListOf<CartLine>()
            for (j in 0 until linesJson.length()) {
                val line = linesJson.optJSONObject(j) ?: continue
                lines += CartLine(
                    id = line.optString("id", UUID.randomUUID().toString()),
                    productId = line.optString("productId", "legacy"),
                    title = line.optString("title", "محصول"),
                    unitPrice = line.optLong("unitPrice", 0),
                    quantity = line.optInt("quantity", 1).coerceAtLeast(1),
                    variant = line.optString("variant", ""),
                    note = line.optString("note", "")
                )
            }
            orders += StoreOrder(
                id = obj.optString("id", "CC"),
                createdAt = obj.optLong("createdAt", 0),
                total = obj.optLong("total", 0),
                status = obj.optString("status", "ثبت شده"),
                customerName = obj.optString("customerName", ""),
                phone = obj.optString("phone", ""),
                city = obj.optString("city", ""),
                address = obj.optString("address", ""),
                postalCode = obj.optString("postalCode", ""),
                notes = obj.optString("notes", ""),
                items = lines
            )
        }
    }

    // داده‌ی خراب به آرایه‌ی خالی تبدیل می‌شود و باعث Crash نمی‌شود.
    private fun readArray(key: String): JSONArray = runCatching {
        JSONArray(prefs.getString(key, "[]").orEmpty().ifBlank { "[]" })
    }.getOrElse { JSONArray() }

    companion object {
        private const val KEY_CART = "cart"
        private const val KEY_DESIGNS = "saved_designs"
        private const val KEY_ORDERS = "orders"
        private const val KEY_POINTS = "loyalty_points"
    }
}

// تمام قیمت‌ها با جداکننده سه‌رقمی نمایش داده می‌شوند.
internal fun formatToman(value: Long): String = "${NumberFormat.getIntegerInstance(Locale.US).format(value)} تومان"

// تخمین قیمت طراحی بر اساس نوع لباس، سطح چاپ و محل چاپ.
internal fun estimateDesignPrice(garment: String, widthCm: Int, heightCm: Int, location: String): Long {
    val garmentPrice = when (garment) {
        "هودی" -> 920_000L
        "شلوار" -> 760_000L
        else -> 500_000L
    }
    val areaPrice = (widthCm.coerceIn(5, 60) * heightCm.coerceIn(5, 80) * 420L).coerceIn(90_000L, 390_000L)
    val locationExtra = when (location) {
        "پشت کامل" -> 90_000L
        "آستین / پاچه" -> 50_000L
        else -> 0L
    }
    return garmentPrice + areaPrice + locationExtra
}

// صفحه‌ی خانه‌ی نسخه کامل؛ همه‌ی مسیرهای اصلی از اینجا در دسترس هستند.
@Composable
internal fun CommerceHomeScreen(store: CactusStore, onCategory: (String) -> Unit, onNavigate: (AppScreen) -> Unit) {
    val categories = listOf(
        CommerceCategory("تیشرت", R.drawable.ic_tshirt, CactusPink),
        CommerceCategory("هودی", R.drawable.ic_hoodie, CactusLavender),
        CommerceCategory("شلوار", R.drawable.ic_pants, CactusYellow),
        CommerceCategory("چاپ اختصاصی", R.drawable.ic_printer, CactusMint),
        CommerceCategory("فروش عمده", R.drawable.ic_boxes, CactusPeach),
        CommerceCategory("طرح‌های آماده", R.drawable.ic_palette, CactusBlue)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(shape = RoundedCornerShape(28.dp), elevation = CardDefaults.cardElevation(5.dp)) {
                Box(
                    Modifier.fillMaxWidth().height(195.dp)
                        .background(Brush.linearGradient(listOf(Color(0xFF6E4AA5), Color(0xFFA477D5), Color(0xFFF0A9CF))))
                ) {
                    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
                        Text("استایل خودت رو بساز", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                        Text("طراحی، چاپ DTF و سفارش در یک مسیر", color = Color.White.copy(alpha = .88f), fontSize = 14.sp)
                        Spacer(Modifier.height(15.dp))
                        Button(
                            onClick = { onNavigate(AppScreen.DESIGN_STUDIO) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7DA7)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, null)
                            Spacer(Modifier.width(7.dp))
                            Text("شروع طراحی", fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        Modifier.align(Alignment.CenterEnd).padding(end = 18.dp).size(108.dp)
                            .clip(CircleShape).background(Color.White.copy(alpha = .18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(R.drawable.ic_hoodie), null, modifier = Modifier.size(84.dp), tint = Color.Unspecified)
                    }
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CommerceQuick("طراحی اختصاصی", Icons.Rounded.AutoAwesome, Modifier.weight(1f)) { onNavigate(AppScreen.DESIGN_STUDIO) }
                CommerceQuick("سبد خرید ${store.cartCount}", Icons.Rounded.ShoppingCart, Modifier.weight(1f)) { onNavigate(AppScreen.CART) }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CommerceQuick("سفارش عمده", Icons.Rounded.Storefront, Modifier.weight(1f)) { onNavigate(AppScreen.WHOLESALE) }
                CommerceQuick("طرح‌های من", Icons.Rounded.Favorite, Modifier.weight(1f)) { onNavigate(AppScreen.SAVED_DESIGNS) }
            }
        }

        item { Text("دسته‌بندی‌ها", fontSize = 20.sp, fontWeight = FontWeight.Black) }
        items(categories.chunked(2)) { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (category in row) {
                    CommerceCategoryCard(category, Modifier.weight(1f)) {
                        when (category.title) {
                            "فروش عمده" -> onNavigate(AppScreen.WHOLESALE)
                            "چاپ اختصاصی" -> onNavigate(AppScreen.DESIGN_STUDIO)
                            else -> onCategory(category.title)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        item { Text("پیشنهادهای فروشگاه", fontSize = 20.sp, fontWeight = FontWeight.Black) }
        items(store.products.take(4)) { product ->
            CommerceProductRow(product) { store.addProduct(product) }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigate(AppScreen.CLUB) },
                colors = CardDefaults.cardColors(containerColor = CactusLavender),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, null, tint = CactusPurple, modifier = Modifier.size(38.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("باشگاه مشتریان", fontWeight = FontWeight.Black)
                        Text("${store.loyaltyPoints} امتیاز • ${store.orders.size} سفارش", color = CactusMuted, fontSize = 11.sp)
                    }
                    Text("مشاهده", color = CactusPurpleDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

private data class CommerceCategory(val title: String, @DrawableRes val icon: Int, val background: Color)

@Composable
private fun CommerceQuick(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(90.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = CactusPurple, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun CommerceCategoryCard(category: CommerceCategory, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(126.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = category.background),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(painterResource(category.icon), null, modifier = Modifier.size(58.dp), tint = Color.Unspecified)
            Spacer(Modifier.height(7.dp))
            Text(category.title, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun CommerceProductRow(product: StoreProduct, onAdd: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(74.dp).clip(RoundedCornerShape(18.dp)).background(CactusLavender.copy(alpha = .6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(product.icon), null, modifier = Modifier.size(52.dp), tint = Color.Unspecified)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.title, fontWeight = FontWeight.ExtraBold)
                Text(product.description, color = CactusMuted, fontSize = 11.sp, lineHeight = 17.sp)
                Spacer(Modifier.height(5.dp))
                Text(formatToman(product.price), color = CactusPurpleDark, fontWeight = FontWeight.Black)
            }
            TextButton(onClick = onAdd) { Text("افزودن", fontSize = 11.sp) }
        }
    }
}

// کاتالوگ هر دسته به سبد واقعی متصل است.
@Composable
internal fun CommerceCatalogScreen(store: CactusStore, category: String, onOpenCart: () -> Unit, onOpenDesign: () -> Unit) {
    val products = remember(category) { store.products.filter { it.category == category } }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CactusLavender), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(category, fontSize = 22.sp, fontWeight = FontWeight.Black, color = CactusPurpleDark)
                    Text("محصول را به سبد اضافه کنید؛ اطلاعات تحویل در مرحله‌ی بعد ثبت می‌شود.", color = CactusMuted, fontSize = 12.sp)
                    if (category == "چاپ اختصاصی") {
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = onOpenDesign, shape = RoundedCornerShape(14.dp)) { Text("باز کردن استودیو طراحی") }
                    }
                }
            }
        }
        if (products.isEmpty()) {
            item { Text("برای این بخش از ابزار تخصصی همان بخش استفاده کنید.", modifier = Modifier.fillMaxWidth().padding(28.dp), textAlign = TextAlign.Center, color = CactusMuted) }
        } else {
            items(products) { product -> CommerceProductRow(product) { store.addProduct(product) } }
        }
        if (store.cartCount > 0) {
            item {
                Button(
                    onClick = onOpenCart,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
                ) { Text("مشاهده سبد (${store.cartCount}) — ${formatToman(store.cartTotal)}") }
            }
        }
    }
}

// جستجوی زنده روی عنوان، دسته و توضیحات محصولات.
@Composable
internal fun CommerceSearchScreen(store: CactusStore, onOpenCart: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val needle = query.trim()
    val results = if (needle.isBlank()) store.products else store.products.filter { product ->
        product.title.contains(needle, ignoreCase = true) ||
            product.category.contains(needle, ignoreCase = true) ||
            product.description.contains(needle, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("جستجو در محصولات") },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                shape = RoundedCornerShape(18.dp)
            )
        }
        if (results.isEmpty()) {
            item { Text("نتیجه‌ای پیدا نشد.", modifier = Modifier.fillMaxWidth().padding(30.dp), textAlign = TextAlign.Center, color = CactusMuted) }
        } else {
            items(results) { product -> CommerceProductRow(product) { store.addProduct(product) } }
        }
        if (store.cartCount > 0) {
            item { OutlinedButton(onClick = onOpenCart, modifier = Modifier.fillMaxWidth()) { Text("باز کردن سبد خرید (${store.cartCount})") } }
        }
    }
}

// سبد خرید با افزایش/کاهش تعداد و حذف آیتم.
@Composable
internal fun CommerceCartScreen(store: CactusStore, onCheckout: () -> Unit, onContinue: () -> Unit) {
    if (store.cart.isEmpty()) {
        CommerceEmpty(Icons.Rounded.ShoppingCart, "سبد خرید خالی است", "از فروشگاه یا استودیو طراحی یک مورد انتخاب کنید.", "رفتن به فروشگاه", onContinue)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(store.cart, key = { it.id }) { line ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(line.title, fontWeight = FontWeight.ExtraBold)
                            if (line.variant.isNotBlank()) Text(line.variant, color = CactusPurple, fontSize = 11.sp)
                            if (line.note.isNotBlank()) Text(line.note, color = CactusMuted, fontSize = 10.sp, lineHeight = 15.sp)
                        }
                        IconButton(onClick = { store.removeFromCart(line.id) }) { Icon(Icons.Rounded.Delete, "حذف", tint = Color(0xFFB74A5A)) }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(formatToman(line.unitPrice * line.quantity), fontWeight = FontWeight.Black, color = CactusPurpleDark)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { store.changeQuantity(line.id, -1) }) { Icon(Icons.Rounded.Remove, "کم کردن") }
                        Text(line.quantity.toString(), fontWeight = FontWeight.Black)
                        IconButton(onClick = { store.changeQuantity(line.id, 1) }) { Icon(Icons.Rounded.Add, "زیاد کردن") }
                    }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CactusLavender), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    CommerceSummary("تعداد اقلام", store.cartCount.toString())
                    Spacer(Modifier.height(8.dp))
                    CommerceSummary("جمع کل", formatToman(store.cartTotal), true)
                }
            }
        }
        item {
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
            ) {
                Icon(Icons.Rounded.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text("ثبت اطلاعات تحویل", fontWeight = FontWeight.Bold)
            }
        }
        item { OutlinedButton(onClick = { store.clearCart() }, modifier = Modifier.fillMaxWidth()) { Text("خالی کردن سبد") } }
    }
}

@Composable
private fun CommerceSummary(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, color = CactusMuted)
        Spacer(Modifier.weight(1f))
        Text(value, fontWeight = if (bold) FontWeight.Black else FontWeight.SemiBold, color = if (bold) CactusPurpleDark else CactusText)
    }
}

// فرم تسویه؛ بدون درگاه جعلی، سفارش را روی دستگاه ثبت می‌کند.
@Composable
internal fun CommerceCheckoutScreen(store: CactusStore, profile: UserProfile, onPlaced: (StoreOrder) -> Unit, onBackCart: () -> Unit) {
    var name by remember { mutableStateOf(profile.fullName) }
    var phone by remember { mutableStateOf(profile.phone) }
    var city by remember { mutableStateOf(profile.city) }
    var address by remember { mutableStateOf("") }
    var postal by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val validPhone = phone.matches(Regex("^09\\d{9}$"))
    val valid = store.cart.isNotEmpty() && name.isNotBlank() && validPhone && city.isNotBlank() && address.length >= 8

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CactusLavender), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("خلاصه سفارش", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(Modifier.height(7.dp))
                    CommerceSummary("${store.cartCount} قلم", formatToman(store.cartTotal), true)
                }
            }
        }
        item { CommerceField(name, { name = it }, "نام و نام خانوادگی") }
        item { CommerceField(phone, { phone = it.filter(Char::isDigit).take(11) }, "شماره موبایل", KeyboardType.Phone) }
        item { CommerceField(city, { city = it }, "شهر") }
        item { CommerceField(address, { address = it }, "آدرس کامل", singleLine = false) }
        item { CommerceField(postal, { postal = it.filter(Char::isDigit).take(10) }, "کد پستی (اختیاری)", KeyboardType.Number) }
        item { CommerceField(notes, { notes = it }, "توضیحات سفارش (اختیاری)", singleLine = false) }
        if (!validPhone && phone.isNotBlank()) {
            item { Text("شماره موبایل باید با 09 شروع شود و 11 رقم باشد.", color = Color(0xFFB74A5A), fontSize = 11.sp) }
        }
        item {
            Button(
                onClick = { store.submitOrder(profile, name, phone, city, address, postal, notes)?.let(onPlaced) },
                enabled = valid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
            ) {
                Icon(Icons.Rounded.LocalMall, null)
                Spacer(Modifier.width(8.dp))
                Text("ثبت نهایی سفارش", fontWeight = FontWeight.Bold)
            }
        }
        item {
            Text(
                "سفارش در این نسخه روی گوشی ذخیره می‌شود. پرداخت آنلاین و وضعیت زنده پس از اتصال سرویس واقعی فعال می‌شوند.",
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = CactusMuted, fontSize = 11.sp, lineHeight = 18.sp
            )
        }
        item { OutlinedButton(onClick = onBackCart, modifier = Modifier.fillMaxWidth()) { Text("بازگشت به سبد") } }
    }
}

@Composable
private fun CommerceField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(17.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

// استودیو طراحی: لباس، رنگ، محل چاپ، ابعاد، متن و فایل کاربر.
@Composable
internal fun DesignStudioScreen(store: CactusStore, onCart: () -> Unit, onSaved: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var garment by remember { mutableStateOf("تیشرت") }
    var garmentColor by remember { mutableStateOf("سفید") }
    var location by remember { mutableStateOf("سینه") }
    var widthText by remember { mutableStateOf("20") }
    var heightText by remember { mutableStateOf("25") }
    var customText by remember { mutableStateOf("") }
    var artworkUri by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf<SavedDesign?>(null) }
    val width = widthText.toIntOrNull()?.coerceIn(5, 60) ?: 20
    val height = heightText.toIntOrNull()?.coerceIn(5, 80) ?: 25
    val estimate = estimateDesignPrice(garment, width, height, location)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> artworkUri = uri?.toString().orEmpty() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CactusLavender), shape = RoundedCornerShape(24.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = CactusPurple, modifier = Modifier.size(38.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("استودیو طراحی CACTUS", fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("مشخصات چاپ را بساز و پروژه را ذخیره کن.", color = CactusMuted, fontSize = 11.sp)
                    }
                }
            }
        }
        item { CommerceField(title, { title = it }, "نام پروژه") }
        item { Text("نوع لباس", fontWeight = FontWeight.ExtraBold); CommerceChoices(listOf("تیشرت", "هودی", "شلوار"), garment) { garment = it } }
        item { Text("رنگ لباس", fontWeight = FontWeight.ExtraBold); CommerceChoices(listOf("سفید", "مشکی", "یاسی", "کرم", "طوسی"), garmentColor) { garmentColor = it } }
        item { Text("محل چاپ", fontWeight = FontWeight.ExtraBold); CommerceChoices(listOf("سینه", "پشت کامل", "آستین / پاچه"), location) { location = it } }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(widthText, { widthText = it.filter(Char::isDigit).take(2) }, Modifier.weight(1f), label = { Text("عرض cm") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(heightText, { heightText = it.filter(Char::isDigit).take(2) }, Modifier.weight(1f), label = { Text("ارتفاع cm") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
        item { CommerceField(customText, { customText = it }, "متن روی لباس (اختیاری)") }
        item {
            OutlinedButton(onClick = { picker.launch("image/*") }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(17.dp)) {
                Icon(Icons.Rounded.Image, null)
                Spacer(Modifier.width(7.dp))
                Text(if (artworkUri.isBlank()) "انتخاب فایل طرح PNG/JPG" else "فایل انتخاب شد — تغییر فایل")
            }
        }
        item { CommerceDesignPreview(garment, garmentColor, location, customText, artworkUri.isNotBlank()) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    CommerceSummary("ابعاد چاپ", "$width × $height سانتی‌متر")
                    Spacer(Modifier.height(7.dp))
                    CommerceSummary("قیمت تقریبی", formatToman(estimate), true)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { saved = store.saveDesign(title, garment, garmentColor, location, width, height, customText, artworkUri) },
                    modifier = Modifier.weight(1f).height(54.dp)
                ) { Text("ذخیره طرح") }
                Button(
                    onClick = {
                        val design = saved ?: store.saveDesign(title, garment, garmentColor, location, width, height, customText, artworkUri)
                        store.addDesignToCart(design)
                        onCart()
                    },
                    modifier = Modifier.weight(1f).height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
                ) { Text("افزودن به سبد") }
            }
        }
        if (saved != null) item { Text("طرح با موفقیت ذخیره شد.", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF3A8C63), fontWeight = FontWeight.Bold) }
        item { TextButton(onClick = onSaved, modifier = Modifier.fillMaxWidth()) { Text("طرح‌های ذخیره‌شده (${store.savedDesigns.size})") } }
    }
}

@Composable
private fun CommerceChoices(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 6.dp)) {
        items(options) { option ->
            if (option == selected) Button(onClick = { onSelect(option) }) { Text(option) }
            else OutlinedButton(onClick = { onSelect(option) }) { Text(option) }
        }
    }
}

@Composable
private fun CommerceDesignPreview(garment: String, colorName: String, location: String, customText: String, hasArtwork: Boolean) {
    val background = when (colorName) {
        "مشکی" -> Color(0xFF2C2930)
        "یاسی" -> Color(0xFFD9C2F0)
        "کرم" -> Color(0xFFF2DFC4)
        "طوسی" -> Color(0xFFCECED3)
        else -> Color.White
    }
    val icon = when (garment) { "هودی" -> R.drawable.ic_hoodie; "شلوار" -> R.drawable.ic_pants; else -> R.drawable.ic_tshirt }
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("پیش‌نمایش مفهومی", fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(20.dp)).background(CactusLavender.copy(alpha = .35f)), contentAlignment = Alignment.Center) {
                Box(Modifier.size(160.dp).clip(RoundedCornerShape(28.dp)).background(background), contentAlignment = Alignment.Center) {
                    Icon(painterResource(icon), null, modifier = Modifier.size(130.dp), tint = Color.Unspecified)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (hasArtwork) Icon(Icons.Rounded.Image, null, tint = CactusPurpleDark, modifier = Modifier.size(27.dp))
                        if (customText.isNotBlank()) Text(customText.take(16), fontWeight = FontWeight.Black, fontSize = 11.sp, color = if (colorName == "مشکی") Color.White else CactusPurpleDark)
                    }
                }
            }
            Spacer(Modifier.height(7.dp))
            Text("$colorName • $location", color = CactusMuted, fontSize = 11.sp)
        }
    }
}

// سفارش عمده با قیمت و تخفیف پلکانی.
@Composable
internal fun WholesaleScreen(store: CactusStore, onCart: () -> Unit) {
    var garment by remember { mutableStateOf("تیشرت") }
    var quantityText by remember { mutableStateOf("20") }
    var printing by remember { mutableStateOf("بدون چاپ") }
    var note by remember { mutableStateOf("") }
    val quantity = quantityText.toIntOrNull()?.coerceIn(10, 1000) ?: 20
    val base = when (garment) { "هودی" -> 760_000L; "شلوار" -> 620_000L; else -> 390_000L }
    val printExtra = when (printing) { "چاپ سینه" -> 120_000L; "چاپ پشت کامل" -> 230_000L; "چاپ دوطرف" -> 310_000L; else -> 0L }
    val discount = when { quantity >= 500 -> 18; quantity >= 200 -> 14; quantity >= 100 -> 10; quantity >= 50 -> 7; quantity >= 20 -> 4; else -> 0 }
    val unitPrice = (base + printExtra) * (100 - discount) / 100

    LazyColumn(Modifier.fillMaxSize().background(CactusBackground), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CactusPeach), shape = RoundedCornerShape(24.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Storefront, null, tint = CactusPurple, modifier = Modifier.size(38.dp))
                    Spacer(Modifier.width(10.dp))
                    Column { Text("محاسبه سفارش عمده", fontWeight = FontWeight.Black, fontSize = 20.sp); Text("قیمت پلکانی بر اساس تعداد", color = CactusMuted, fontSize = 11.sp) }
                }
            }
        }
        item { Text("نوع پوشاک", fontWeight = FontWeight.ExtraBold); CommerceChoices(listOf("تیشرت", "هودی", "شلوار"), garment) { garment = it } }
        item { OutlinedTextField(quantityText, { quantityText = it.filter(Char::isDigit).take(4) }, Modifier.fillMaxWidth(), label = { Text("تعداد 10 تا 1000") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }
        item { Text("نوع چاپ", fontWeight = FontWeight.ExtraBold); CommerceChoices(listOf("بدون چاپ", "چاپ سینه", "چاپ پشت کامل", "چاپ دوطرف"), printing) { printing = it } }
        item { CommerceField(note, { note = it }, "توضیحات (اختیاری)", singleLine = false) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CactusLavender), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    CommerceSummary("تخفیف تعداد", "$discount٪")
                    Spacer(Modifier.height(7.dp)); CommerceSummary("قیمت هر عدد", formatToman(unitPrice))
                    Spacer(Modifier.height(7.dp)); CommerceSummary("جمع کل", formatToman(unitPrice * quantity), true)
                }
            }
        }
        item {
            Button(
                onClick = {
                    store.addCustomLine("سفارش عمده $garment", unitPrice, quantity, "$printing / تعداد $quantity", note, "wholesale:$garment")
                    onCart()
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
            ) { Text("افزودن سفارش عمده به سبد", fontWeight = FontWeight.Bold) }
        }
    }
}

// فهرست پروژه‌های طراحی ذخیره‌شده.
@Composable
internal fun SavedDesignsScreen(store: CactusStore, onStudio: () -> Unit, onCart: () -> Unit) {
    if (store.savedDesigns.isEmpty()) {
        CommerceEmpty(Icons.Rounded.AutoAwesome, "هنوز طرحی ذخیره نشده", "از استودیو طراحی اولین پروژه را بسازید.", "ساخت طرح", onStudio)
        return
    }
    LazyColumn(Modifier.fillMaxSize().background(CactusBackground), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(store.savedDesigns, key = { it.id }) { design ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AutoAwesome, null, tint = CactusPurple, modifier = Modifier.size(42.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(design.title, fontWeight = FontWeight.Black)
                            Text("${design.garment} • ${design.garmentColor} • ${design.printLocation}", color = CactusMuted, fontSize = 11.sp)
                            Text("${design.widthCm}×${design.heightCm} cm — ${formatToman(design.estimatedPrice)}", color = CactusPurpleDark, fontSize = 11.sp)
                        }
                        IconButton(onClick = { store.deleteDesign(design.id) }) { Icon(Icons.Rounded.Delete, "حذف", tint = Color(0xFFB74A5A)) }
                    }
                    Spacer(Modifier.height(9.dp))
                    Button(
                        onClick = { store.addDesignToCart(design); onCart() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("سفارش این طرح") }
                }
            }
        }
    }
}

// تاریخچه سفارش‌ها با سفارش مجدد و اشتراک‌گذاری.
@Composable
internal fun CommerceOrdersScreen(store: CactusStore, onCart: () -> Unit) {
    val context = LocalContext.current
    if (store.orders.isEmpty()) {
        CommerceEmpty(Icons.Rounded.Description, "هنوز سفارشی ثبت نشده", "سفارش‌های نهایی شما اینجا نگهداری می‌شوند.", "مشاهده سبد خرید", onCart)
        return
    }
    LazyColumn(Modifier.fillMaxSize().background(CactusBackground), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        items(store.orders, key = { it.id }) { order ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text(order.id, fontWeight = FontWeight.Black, color = CactusPurpleDark); Text(formatOrderDate(order.createdAt), color = CactusMuted, fontSize = 11.sp) }
                        Text(order.status, color = Color(0xFF3A8C63), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(progress = 0.25f, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)), color = CactusPurple, trackColor = CactusLavender)
                    Spacer(Modifier.height(10.dp))
                    for (line in order.items.take(3)) Text("• ${line.title} × ${line.quantity}", fontSize = 11.sp)
                    Spacer(Modifier.height(9.dp)); CommerceSummary("مبلغ سفارش", formatToman(order.total), true)
                    Spacer(Modifier.height(9.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { store.reorder(order); onCart() }, modifier = Modifier.weight(1f)) { Icon(Icons.Rounded.Replay, null, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(4.dp)); Text("سفارش مجدد", fontSize = 11.sp) }
                        OutlinedButton(onClick = { shareOrder(context, order) }, modifier = Modifier.weight(1f)) { Icon(Icons.Rounded.Share, null, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(4.dp)); Text("اشتراک", fontSize = 11.sp) }
                    }
                }
            }
        }
    }
}

// باشگاه مشتریان بر اساس داده‌های واقعی محلی، نه اعداد نمایشی ثابت.
@Composable
internal fun CommerceClubScreen(store: CactusStore, onOrders: () -> Unit, onDesigns: () -> Unit) {
    val level = when { store.loyaltyPoints >= 1000 -> "طلایی"; store.loyaltyPoints >= 400 -> "نقره‌ای"; else -> "عضو" }
    val target = when { store.loyaltyPoints >= 1000 -> 1500; store.loyaltyPoints >= 400 -> 1000; else -> 400 }
    val progress = (store.loyaltyPoints.toFloat() / target).coerceIn(0f, 1f)
    LazyColumn(Modifier.fillMaxSize().background(CactusBackground), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(shape = RoundedCornerShape(26.dp)) {
                Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF65429F), Color(0xFF986FD0)))).padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Verified, null, tint = Color.White, modifier = Modifier.size(42.dp)); Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) { Text("باشگاه مشتریان CACTUS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 19.sp); Text("سطح $level", color = Color.White.copy(alpha = .85f), fontSize = 12.sp) }
                        Text("${store.loyaltyPoints} ★", color = Color.White, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(14.dp)); LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)), color = Color(0xFFFFD166), trackColor = Color.White.copy(alpha = .2f))
                    Spacer(Modifier.height(5.dp)); Text("${max(0, target - store.loyaltyPoints)} امتیاز تا مرحله بعد", color = Color.White.copy(alpha = .85f), fontSize = 10.sp)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CommerceStat("سفارش‌ها", store.orders.size.toString(), Modifier.weight(1f), onOrders)
                CommerceStat("طرح‌ها", store.savedDesigns.size.toString(), Modifier.weight(1f), onDesigns)
                CommerceStat("امتیاز", store.loyaltyPoints.toString(), Modifier.weight(1f)) {}
            }
        }
        item { CommerceBenefit("خرید و امتیاز", "به ازای هر 100,000 تومان خرید، امتیاز دریافت می‌کنید.", Icons.Rounded.Star) }
        item { CommerceBenefit("سفارش مجدد", "سفارش قبلی را با یک لمس به سبد برگردانید.", Icons.Rounded.Replay) }
        item { CommerceBenefit("طرح‌های ذخیره‌شده", "طرح اختصاصی را نگه دارید و دوباره سفارش دهید.", Icons.Rounded.AutoAwesome) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CactusMint), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("سطوح پیشنهادی جوایز", fontWeight = FontWeight.Black, color = CactusPurpleDark)
                    Text("400 امتیاز: تخفیف چاپ کوچک\n1000 امتیاز: تخفیف سفارش اختصاصی\n1500 امتیاز: مزیت ارسال یا هدیه کالکشن", color = CactusMuted, fontSize = 12.sp, lineHeight = 21.sp)
                }
            }
        }
    }
}

@Composable
private fun CommerceStat(title: String, value: String, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.height(90.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(value, fontWeight = FontWeight.Black, color = CactusPurpleDark, fontSize = 19.sp); Text(title, color = CactusMuted, fontSize = 10.sp) }
    }
}

@Composable
private fun CommerceBenefit(title: String, body: String, icon: ImageVector) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(CactusLavender), contentAlignment = Alignment.Center) { Icon(icon, null, tint = CactusPurple) }
            Spacer(Modifier.width(10.dp)); Column { Text(title, fontWeight = FontWeight.ExtraBold); Text(body, color = CactusMuted, fontSize = 11.sp, lineHeight = 17.sp) }
        }
    }
}

@Composable
private fun CommerceEmpty(icon: ImageVector, title: String, body: String, action: String, onAction: () -> Unit) {
    Column(Modifier.fillMaxSize().background(CactusBackground).padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(92.dp).clip(CircleShape).background(CactusLavender), contentAlignment = Alignment.Center) { Icon(icon, null, tint = CactusPurple, modifier = Modifier.size(48.dp)) }
        Spacer(Modifier.height(18.dp)); Text(title, fontWeight = FontWeight.Black, fontSize = 21.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp)); Text(body, color = CactusMuted, fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 21.sp)
        Spacer(Modifier.height(20.dp)); Button(onClick = onAction, colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)) { Text(action) }
    }
}

private fun formatOrderDate(time: Long): String = if (time <= 0) "" else SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.US).format(Date(time))

// اشتراک خلاصه سفارش از Share Sheet استاندارد اندروید.
private fun shareOrder(context: Context, order: StoreOrder) {
    val itemsText = order.items.joinToString("\n") { "• ${it.title} × ${it.quantity}" }
    val message = """
        سفارش ${order.id}
        نام: ${order.customerName}
        موبایل: ${order.phone}
        شهر: ${order.city}
        آدرس: ${order.address}

        $itemsText

        جمع کل: ${formatToman(order.total)}
        وضعیت: ${order.status}
    """.trimIndent()
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "سفارش CACTUS Collection ${order.id}")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(intent, "اشتراک سفارش"))
}
