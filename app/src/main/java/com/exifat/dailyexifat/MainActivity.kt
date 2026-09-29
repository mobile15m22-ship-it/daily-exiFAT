package com.exifat.dailyexifat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.room.*

// --- DATABASE ---
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: String,
    val note: String,
    val date: Long = System.currentTimeMillis()
)

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAll(): kotlinx.coroutines.flow.Flow<List<Expense>>
    @Insert suspend fun insert(e: Expense)
    @Delete suspend fun delete(e: Expense)
    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotal(): kotlinx.coroutines.flow.Flow<Double?>
}

@Database(entities = [Expense::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): ExpenseDao
}

// --- UI ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF2C1E13), secondary = Color(0xFFC5A880)
            )) {
                DailyExiFATApp()
            }
        }
    }
}

@Composable
fun DailyExiFATApp() {
    var salary by remember { mutableStateOf(35000.0) }
    var showAdd by remember { mutableStateOf(false) }
    // এখানে Room, ViewModel, Chart, Wallet Card এর পুরো UI কোড থাকবে
    // Dashboard Card -> Wallet Style
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { showAdd = true }, containerColor = Color(0xFF2C1E13)) {
            Text("+", color = Color.White)
        }
    }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // WALLET CARD
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1E13))) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("মোট স্যালারি", color = Color(0xFFC5A880))
                    Text("৳ $salary", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(progress = 0.65f, modifier = Modifier.fillMaxWidth().height(8.dp))
                    Text("হাতে আছে: ৳ ${salary - 12500}", color = Color.White, modifier = Modifier.padding(top=8.dp))
                }
            }
            // এখানে PieChart + History List আসবে
        }
    }
}
