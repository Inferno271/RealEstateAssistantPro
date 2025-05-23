package com.realestateassistant.pro.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.realestateassistant.pro.data.local.converters.AppointmentStatusConverter
import com.realestateassistant.pro.data.local.converters.AppointmentTypeConverter
import com.realestateassistant.pro.data.local.converters.ListStringConverter
import com.realestateassistant.pro.data.local.converters.SeasonalPriceListConverter
import com.realestateassistant.pro.data.local.dao.AppointmentDao
import com.realestateassistant.pro.data.local.dao.ClientDao
import com.realestateassistant.pro.data.local.dao.PropertyDao
import com.realestateassistant.pro.data.local.dao.UserDao
import com.realestateassistant.pro.data.local.entity.AppointmentEntity
import com.realestateassistant.pro.data.local.entity.ClientEntity
import com.realestateassistant.pro.data.local.entity.PropertyEntity
import com.realestateassistant.pro.data.local.entity.UserEntity
import com.realestateassistant.pro.data.local.security.DatabaseEncryption
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Inject
import java.io.File

/**
 * Класс базы данных Room для приложения
 * 
 * База данных использует базовое шифрование SQLCipher для защиты данных.
 * Пароль хранится в защищенном хранилище приложения.
 */
@Database(
    entities = [
        PropertyEntity::class,
        ClientEntity::class,
        AppointmentEntity::class,
        UserEntity::class
    ],
    version = 6,
    exportSchema = true
)
@TypeConverters(
    ListStringConverter::class,
    SeasonalPriceListConverter::class,
    AppointmentStatusConverter::class,
    AppointmentTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Получение DAO для объектов недвижимости
     */
    abstract fun propertyDao(): PropertyDao
    
    /**
     * Получение DAO для клиентов
     */
    abstract fun clientDao(): ClientDao
    
    /**
     * Получение DAO для встреч
     */
    abstract fun appointmentDao(): AppointmentDao
    
    /**
     * Получение DAO для пользователей
     */
    abstract fun userDao(): UserDao
    
    companion object {
        private const val DATABASE_NAME = "realestate_database"
        
        // Миграция с версии 1 на версию 2 (добавление сезонных цен)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Добавляем новые поля в таблицу properties
                database.execSQL("ALTER TABLE properties ADD COLUMN winterMonthlyRent REAL")
                database.execSQL("ALTER TABLE properties ADD COLUMN summerMonthlyRent REAL")
            }
        }
        
        // Миграция с версии 2 на версию 3 (добавление полей для разных типов недвижимости)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Добавляем новые поля в таблицу properties для разных типов недвижимости
                database.execSQL("ALTER TABLE properties ADD COLUMN levelsCount INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE properties ADD COLUMN landArea REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE properties ADD COLUMN hasGarage INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE properties ADD COLUMN garageSpaces INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE properties ADD COLUMN hasBathhouse INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE properties ADD COLUMN hasPool INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE properties ADD COLUMN poolType TEXT NOT NULL DEFAULT ''")
            }
        }
        
        // Миграция с версии 3 на версию 4 (добавление координат)
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Добавляем поля координат в таблицу properties
                database.execSQL("ALTER TABLE properties ADD COLUMN latitude REAL DEFAULT NULL")
                database.execSQL("ALTER TABLE properties ADD COLUMN longitude REAL DEFAULT NULL")
            }
        }
        
        // Миграция с версии 4 на версию 5 (удаление устаревшего поля depositMonths)
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // В SQLite нет прямого способа удалить колонку, поэтому создаем новую таблицу без этого поля
                // Создаем временную таблицу без устаревшего поля
                database.execSQL(
                    "CREATE TABLE properties_new (" +
                    "id TEXT NOT NULL PRIMARY KEY, " +
                    "contactName TEXT NOT NULL, " +
                    "contactPhone TEXT NOT NULL, " +
                    "additionalContactInfo TEXT, " +
                    "propertyType TEXT NOT NULL, " +
                    "address TEXT NOT NULL, " +
                    "latitude REAL, " +
                    "longitude REAL, " +
                    "district TEXT NOT NULL, " +
                    "nearbyObjects TEXT NOT NULL, " +
                    "views TEXT NOT NULL, " +
                    "area REAL NOT NULL, " +
                    "roomsCount INTEGER NOT NULL, " +
                    "isStudio INTEGER NOT NULL, " +
                    "layout TEXT NOT NULL, " +
                    "floor INTEGER NOT NULL, " +
                    "totalFloors INTEGER NOT NULL, " +
                    "description TEXT, " +
                    "management TEXT NOT NULL, " +
                    "levelsCount INTEGER NOT NULL, " +
                    "landArea REAL NOT NULL, " +
                    "hasGarage INTEGER NOT NULL, " +
                    "garageSpaces INTEGER NOT NULL, " +
                    "hasBathhouse INTEGER NOT NULL, " +
                    "hasPool INTEGER NOT NULL, " +
                    "poolType TEXT NOT NULL, " +
                    "repairState TEXT NOT NULL, " +
                    "bedsCount INTEGER, " +
                    "bathroomsCount INTEGER, " +
                    "bathroomType TEXT NOT NULL, " +
                    "noFurniture INTEGER NOT NULL, " +
                    "hasAppliances INTEGER NOT NULL, " +
                    "heatingType TEXT NOT NULL, " +
                    "balconiesCount INTEGER NOT NULL, " +
                    "elevatorsCount INTEGER, " +
                    "hasParking INTEGER NOT NULL, " +
                    "parkingType TEXT, " +
                    "parkingSpaces INTEGER, " +
                    "amenities TEXT NOT NULL, " +
                    "smokingAllowed INTEGER NOT NULL, " +
                    "childrenAllowed INTEGER NOT NULL, " +
                    "minChildAge TEXT, " +
                    "maxChildrenCount TEXT, " +
                    "petsAllowed INTEGER NOT NULL, " +
                    "maxPetsCount TEXT, " +
                    "allowedPetTypes TEXT NOT NULL, " +
                    "monthlyRent REAL, " +
                    "winterMonthlyRent REAL, " +
                    "summerMonthlyRent REAL, " +
                    "hasCompensationContract INTEGER NOT NULL, " +
                    "isSelfEmployed INTEGER NOT NULL, " +
                    "isPersonalIncomeTax INTEGER NOT NULL, " +
                    "isCompanyIncomeTax INTEGER NOT NULL, " +
                    "utilitiesIncluded INTEGER NOT NULL, " +
                    "utilitiesCost REAL, " +
                    "minRentPeriod TEXT, " +
                    "maxRentPeriod INTEGER, " +
                    "depositCustomAmount REAL, " +
                    "securityDeposit REAL, " +
                    "additionalExpenses TEXT, " +
                    "longTermRules TEXT, " +
                    "dailyPrice REAL, " +
                    "minStayDays INTEGER, " +
                    "maxStayDays INTEGER, " +
                    "maxGuests INTEGER, " +
                    "checkInTime TEXT, " +
                    "checkOutTime TEXT, " +
                    "shortTermDeposit REAL, " +
                    "shortTermDepositCustomAmount REAL, " +
                    "seasonalPrices TEXT NOT NULL, " +
                    "weekdayPrice REAL, " +
                    "weekendPrice REAL, " +
                    "weeklyDiscount REAL, " +
                    "monthlyDiscount REAL, " +
                    "additionalServices TEXT, " +
                    "shortTermRules TEXT, " +
                    "cleaningService INTEGER NOT NULL, " +
                    "cleaningDetails TEXT, " +
                    "hasExtraBed INTEGER NOT NULL, " +
                    "extraBedPrice REAL, " +
                    "partiesAllowed INTEGER NOT NULL, " +
                    "specialOffers TEXT, " +
                    "additionalComments TEXT, " +
                    "photos TEXT NOT NULL, " +
                    "documents TEXT NOT NULL, " +
                    "createdAt INTEGER NOT NULL, " +
                    "updatedAt INTEGER NOT NULL, " +
                    "isSynced INTEGER NOT NULL DEFAULT 0)"
                )
                
                // Копируем данные из старой таблицы в новую, игнорируя поле depositMonths
                database.execSQL(
                    "INSERT INTO properties_new SELECT " +
                    "id, contactName, contactPhone, additionalContactInfo, propertyType, address, latitude, longitude, " +
                    "district, nearbyObjects, views, area, roomsCount, isStudio, layout, floor, totalFloors, " +
                    "description, management, levelsCount, landArea, hasGarage, garageSpaces, hasBathhouse, hasPool, " +
                    "poolType, repairState, bedsCount, bathroomsCount, bathroomType, noFurniture, hasAppliances, " +
                    "heatingType, balconiesCount, elevatorsCount, hasParking, parkingType, parkingSpaces, amenities, " +
                    "smokingAllowed, childrenAllowed, minChildAge, maxChildrenCount, petsAllowed, maxPetsCount, " +
                    "allowedPetTypes, monthlyRent, winterMonthlyRent, summerMonthlyRent, hasCompensationContract, " +
                    "isSelfEmployed, isPersonalIncomeTax, isCompanyIncomeTax, utilitiesIncluded, utilitiesCost, " +
                    "minRentPeriod, maxRentPeriod, depositCustomAmount, securityDeposit, additionalExpenses, longTermRules, " +
                    "dailyPrice, minStayDays, maxStayDays, maxGuests, checkInTime, checkOutTime, shortTermDeposit, " +
                    "shortTermDepositCustomAmount, seasonalPrices, weekdayPrice, weekendPrice, weeklyDiscount, " +
                    "monthlyDiscount, additionalServices, shortTermRules, cleaningService, cleaningDetails, hasExtraBed, " +
                    "extraBedPrice, partiesAllowed, specialOffers, additionalComments, photos, documents, createdAt, " +
                    "updatedAt, isSynced FROM properties"
                )
                
                // Удаляем старую таблицу
                database.execSQL("DROP TABLE properties")
                
                // Переименовываем новую таблицу
                database.execSQL("ALTER TABLE properties_new RENAME TO properties")
            }
        }
        
        // Миграция с версии 5 на версию 6 (добавление полей клиента)
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE clients ADD COLUMN childrenAge TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE clients ADD COLUMN moveInDeadline INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE clients ADD COLUMN familyComposition TEXT DEFAULT NULL")
            }
        }
        
        // Синглтон для базы данных
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Получение экземпляра базы данных
         */
        fun getInstance(context: Context, databaseEncryption: DatabaseEncryption): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
                    val factory = SupportFactory(passphrase)
                    
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )
                    .openHelperFactory(factory)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .build()
                    
                    // Проверка базы данных на целостность
                    try {
                        // Эта операция вызовет исключение, если база повреждена
                        instance.openHelper.readableDatabase
                        instance.openHelper.writableDatabase
                    } catch (e: Exception) {
                        Log.e("AppDatabase", "База данных повреждена, создаем новую", e)
                        // Закрываем поврежденную базу
                        instance.close()
                        
                        // Удаляем файл базы данных
                        val dbFile = context.getDatabasePath(DATABASE_NAME)
                        if (dbFile.exists()) {
                            dbFile.delete()
                            
                            // Удаляем также файлы журнала и шему
                            val journalFile = File(dbFile.path + "-journal")
                            if (journalFile.exists()) journalFile.delete()
                            
                            val shmFile = File(dbFile.path + "-shm")
                            if (shmFile.exists()) shmFile.delete()
                            
                            val walFile = File(dbFile.path + "-wal")
                            if (walFile.exists()) walFile.delete()
                        }
                        
                        // Создаем новую базу с режимом fallbackToDestructiveMigration
                        return@synchronized Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            DATABASE_NAME
                        )
                        .openHelperFactory(factory)
                        .fallbackToDestructiveMigration() // Разрешаем пересоздание базы
                        .build()
                    }
                    
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    Log.e("AppDatabase", "Ошибка при создании базы данных", e)
                    
                    // В случае любых других ошибок создаем новую базу
                    val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
                    val factory = SupportFactory(passphrase)
                    
                    // Удаляем существующую базу данных
                    val dbFile = context.getDatabasePath(DATABASE_NAME)
                    if (dbFile.exists()) {
                        dbFile.delete()
                        
                        // Удаляем также файлы журнала и шему
                        val journalFile = File(dbFile.path + "-journal")
                        if (journalFile.exists()) journalFile.delete()
                        
                        val shmFile = File(dbFile.path + "-shm")
                        if (shmFile.exists()) shmFile.delete()
                        
                        val walFile = File(dbFile.path + "-wal")
                        if (walFile.exists()) walFile.delete()
                    }
                    
                    // Создаем новую базу
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration() // Разрешаем пересоздание базы
                    .build()
                    
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
} 