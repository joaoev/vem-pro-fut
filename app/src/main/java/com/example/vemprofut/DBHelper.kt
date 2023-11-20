package com.example.vemprofut

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "vemprofut.db"
        private const val DATABASE_VERSION = 9
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableUser = """
            CREATE TABLE Users (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                fullname TEXT, 
                nickname TEXT, 
                email TEXT UNIQUE, 
                password TEXT, 
                city TEXT, 
                state TEXT)"""

        val createTableLocator = """
            CREATE TABLE Locators (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                fullname TEXT, 
                cnpjcpf TEXT, 
                company_name TEXT, 
                email TEXT UNIQUE, 
                phone TEXT, 
                password TEXT, 
                city TEXT, 
                state TEXT)"""

        val createTableLocal = """
            CREATE TABLE Local (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_amenities INTEGER,
                id_locators INTEGER,
                photo_of_the_place BLOB,
                local_name TEXT,
                address TEXT,
                description TEXT,
                hourly_rate REAL,
                FOREIGN KEY (id_amenities) REFERENCES Amenities(id),
                FOREIGN KEY (id_locators) REFERENCES Locators(id))"""

        val createTableAmenities = """
            CREATE TABLE Amenities (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                parking TEXT,
                locker_room TEXT,
                pub TEXT)"""

        val createTableSchedules = """
            CREATE TABLE Schedules (
                id_local INTEGER,
                day_of_the_week TEXT,
                start_time INT,
                end_time INT,
                checked INT,
                FOREIGN KEY (id_local) REFERENCES Local(id))"""

        val createTableAgenda = """
    CREATE TABLE Agenda (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        id_user INTEGER,
        id_local INTEGER,
        date TEXT,
        day_of_the_week TEXT,
        start_time INT,
        end_time INT,
        FOREIGN KEY (id_user) REFERENCES Users(id),
        FOREIGN KEY (id_local) REFERENCES Local(id)
    )
"""




        db.execSQL(createTableUser)
        db.execSQL(createTableLocator)
        db.execSQL(createTableLocal)
        db.execSQL(createTableAmenities)
        db.execSQL(createTableSchedules)
        db.execSQL(createTableAgenda)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Users")
        db.execSQL("DROP TABLE IF EXISTS Locators")
        db.execSQL("DROP TABLE IF EXISTS Local")
        db.execSQL("DROP TABLE IF EXISTS Amenities")
        db.execSQL("DROP TABLE IF EXISTS Schedules")
        db.execSQL("DROP TABLE IF EXISTS Agenda")
        onCreate(db)
    }

    fun insertUser(fullname: String,
                   nickname: String,
                   email: String,
                   password: String,
                   city: String,
                   state : String): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("fullname", fullname)
        contentValues.put("email", email)
        contentValues.put("nickname", nickname)
        contentValues.put("password", password)
        contentValues.put("city", city)
        contentValues.put("state", state)

        val result = db.insert("Users", null, contentValues)

        db.close()

        return result
    }

    fun insertLocator(fullname: String,
                      cnpjcpf: String,
                      companyName: String,
                      email: String,
                      phone: String,
                      password: String,
                      city: String,
                      state : String): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("fullname", fullname)
        contentValues.put("cnpjcpf", cnpjcpf)
        contentValues.put("company_name", companyName)
        contentValues.put("email", email)
        contentValues.put("phone", phone)
        contentValues.put("password", password)
        contentValues.put("city", city)
        contentValues.put("state", state)

        val result = db.insert("Locators", null, contentValues)

        db.close()

        return result
    }

    fun insertLocalAndAmenities(
        id_locators: Int,
        photo_of_the_place: String,
        local_name: String,
        address: String,
        description: String,
        hourly_rate: Double,
        parking: String,
        locker_room: String,
        pub: String,
        horarios: ArrayList<HorarioData>
    ): Long {
        val db = writableDatabase
        val contentValues = ContentValues()

        contentValues.put("id_locators", id_locators)
        contentValues.put("photo_of_the_place", photo_of_the_place)
        contentValues.put("local_name", local_name)
        contentValues.put("address", address)
        contentValues.put("description", description)
        contentValues.put("hourly_rate", hourly_rate)

        var localId: Long = -1

        db.beginTransaction()

        try {
            localId = db.insert("Local", null, contentValues)

            if (localId != -1L) {
                val contentValuesAmenities = ContentValues()

                contentValuesAmenities.put("parking", parking)
                contentValuesAmenities.put("locker_room", locker_room)
                contentValuesAmenities.put("pub", pub)

                val amenitiesId = db.insert("Amenities", null, contentValuesAmenities)

                val updateValues = ContentValues()
                updateValues.put("id_amenities", amenitiesId)
                db.update("Local", updateValues, "id=?", arrayOf(localId.toString()))

                // Adicionar horários à tabela Schedules se houver algum
                if (horarios.isNotEmpty()) {
                    for (horario in horarios) {
                        val contentValuesSchedules = ContentValues()
                        contentValuesSchedules.put("id_local", localId.toInt())
                        contentValuesSchedules.put("day_of_the_week", horario.day_of_the_week)
                        contentValuesSchedules.put("start_time", horario.start_time)
                        contentValuesSchedules.put("end_time", horario.end_time)
                        contentValuesSchedules.put("checked", horario.cheked)
                        db.insert("Schedules", null, contentValuesSchedules)
                    }
                }

                db.setTransactionSuccessful()
            }
        } finally {
            db.endTransaction()
        }

        db.close()

        return localId
    }

    fun insertAgendamento(idUser: Int, idLocal: Int, date: String, dayOfWeek: String, startTime: Int, endTime: Int): Long {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("id_user", idUser)
            put("id_local", idLocal)
            put("date", date)
            put("day_of_the_week", dayOfWeek)
            put("start_time", startTime)
            put("end_time", endTime)
        }

        val id = db.insert("Agenda", null, values)

        db.close()

        return id
    }


//    fun insertSchedules(id_local : Int,
//                        day_of_the_week: String,
//                        start_time : Int,
//                        end_time : Int,
//                        checked: Int
//                        ): Long {
//
//        val db = writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put("id_local", id_local)
//        contentValues.put("day_of_the_week", day_of_the_week)
//        contentValues.put("start_time", start_time)
//        contentValues.put("end_time", end_time)
//        contentValues.put("checked", checked)
//
//        val result = db.insert("Schedules", null, contentValues)
//
//        db.close()
//
//        return result
//
//    }

    fun getLocatorByEmailAndPassword(email: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM Locators WHERE email = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        val locatorExists = cursor.count > 0

        cursor.close()
        db.close()

        return locatorExists
    }

    fun getUserByEmailAndPassword(email: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM Users WHERE email = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        val userExists = cursor.count > 0

        cursor.close()
        db.close()

        return userExists
    }

    fun getUserIdByEmail(email: String): Int? {
        val db = readableDatabase
        val query = "SELECT id FROM Users WHERE email = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        var userId: Int? = null

        println(cursor)

        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex("id")
                if (columnIndex != -1) {
                    userId = cursor.getInt(columnIndex)
                }
            }
        }

        db.close()

        return userId
    }

    fun getLocatorIdByEmail(email: String): Int? {
        val db = readableDatabase
        val query = "SELECT id FROM Locators WHERE email = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        var userId: Int? = null

        println(cursor)

        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex("id")
                if (columnIndex != -1) {
                    userId = cursor.getInt(columnIndex)
                }
            }
        }

        db.close()

        return userId
    }
    fun getUserById(id: Int): Jogador? {
        val db = readableDatabase
        val query = "SELECT * FROM Users WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        var jogador: Jogador? = null

        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"))
                val nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                val city = cursor.getString(cursor.getColumnIndexOrThrow("city"))
                val state = cursor.getString(cursor.getColumnIndexOrThrow("state"))

                jogador = Jogador(fullname, nickname, email, password, city, state)
            }
        }

        db.close()

        return jogador
    }

    fun getLocatorById(id: Int): Locador? {
        val db = readableDatabase
        val query = "SELECT * FROM Locators WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        var locador: Locador? = null

        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"))
                val cnpjcpf = cursor.getString(cursor.getColumnIndexOrThrow("cnpjcpf"))
                val company_name = cursor.getString(cursor.getColumnIndexOrThrow("company_name"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                val city = cursor.getString(cursor.getColumnIndexOrThrow("city"))
                val state = cursor.getString(cursor.getColumnIndexOrThrow("state"))

                locador = Locador(fullname, cnpjcpf,company_name, email, phone, password, city, state)
            }
        }

        db.close()

        return locador
    }

    fun getAllCamposByLocatorId(locatorId: Int): ArrayList<CampoData> {
        val camposList: ArrayList<CampoData> = ArrayList()
        val db = readableDatabase

        val query = """
        SELECT * FROM Local 
        INNER JOIN Amenities ON Local.id_amenities = Amenities.id
        WHERE id_locators = ?
    """
        val cursor = db.rawQuery(query, arrayOf(locatorId.toString()))

        try {
            while (cursor.moveToNext()) {
                val idLocalIndex = cursor.getColumnIndex("id")
                val idLocatorsIndex = cursor.getColumnIndex("id_locators")
                val photoIndex = cursor.getColumnIndex("photo_of_the_place")
                val localNameIndex = cursor.getColumnIndex("local_name")
                val addressIndex = cursor.getColumnIndex("address")
                val descriptionIndex = cursor.getColumnIndex("description")
                val hourlyRateIndex = cursor.getColumnIndex("hourly_rate")
                val parkingIndex = cursor.getColumnIndex("parking")
                val lockerRoomIndex = cursor.getColumnIndex("locker_room")
                val pubIndex = cursor.getColumnIndex("pub")

                // Verifica se os índices são válidos antes de acessar o Cursor
                if (idLocatorsIndex >= 0 && photoIndex >= 0 && localNameIndex >= 0 && addressIndex >= 0 &&
                    descriptionIndex >= 0 && hourlyRateIndex >= 0 && parkingIndex >= 0 &&
                    lockerRoomIndex >= 0 && pubIndex >= 0) {

                    val campo = CampoData(
                        cursor.getInt(idLocalIndex),
                        cursor.getInt(idLocatorsIndex),
                        cursor.getString(photoIndex),
                        cursor.getString(localNameIndex),
                        cursor.getString(addressIndex),
                        cursor.getString(descriptionIndex),
                        cursor.getDouble(hourlyRateIndex),
                        cursor.getString(parkingIndex),
                        cursor.getString(lockerRoomIndex),
                        cursor.getString(pubIndex)
                    )
                    camposList.add(campo)
                } else {

                }
            }
        } finally {
            cursor.close()
        }

        db.close()

        return camposList
    }

    fun getCampoByIdAndLocatorId(campoId: Int, locatorId: Int): CampoData? {
        var campo: CampoData? = null
        val db = readableDatabase

        val query = """
        SELECT * FROM Local 
        INNER JOIN Amenities ON Local.id_amenities = Amenities.id
        WHERE Local.id_locators = ? AND Local.id = ?
    """
        val cursor = db.rawQuery(query, arrayOf(locatorId.toString(), campoId.toString()))

        try {
            if (cursor.moveToNext()) {
                val idLocalIndex = cursor.getColumnIndex("id")
                val idLocatorsIndex = cursor.getColumnIndex("id_locators")
                val photoIndex = cursor.getColumnIndex("photo_of_the_place")
                val localNameIndex = cursor.getColumnIndex("local_name")
                val addressIndex = cursor.getColumnIndex("address")
                val descriptionIndex = cursor.getColumnIndex("description")
                val hourlyRateIndex = cursor.getColumnIndex("hourly_rate")
                val parkingIndex = cursor.getColumnIndex("parking")
                val lockerRoomIndex = cursor.getColumnIndex("locker_room")
                val pubIndex = cursor.getColumnIndex("pub")

                // Verifica se os índices são válidos antes de acessar o Cursor
                if (idLocatorsIndex >= 0 && photoIndex >= 0 && localNameIndex >= 0 && addressIndex >= 0 &&
                    descriptionIndex >= 0 && hourlyRateIndex >= 0 && parkingIndex >= 0 &&
                    lockerRoomIndex >= 0 && pubIndex >= 0) {

                    campo = CampoData(
                        cursor.getInt(idLocalIndex),
                        cursor.getInt(idLocatorsIndex),
                        cursor.getString(photoIndex),
                        cursor.getString(localNameIndex),
                        cursor.getString(addressIndex),
                        cursor.getString(descriptionIndex),
                        cursor.getDouble(hourlyRateIndex),
                        cursor.getString(parkingIndex),
                        cursor.getString(lockerRoomIndex),
                        cursor.getString(pubIndex)
                    )
                } else {

                }
            }
        } finally {
            cursor.close()
        }

        db.close()

        return campo
    }

    fun getScheduleByCampoId(campoId: Int): List<HorarioData> {
        val horariosList: MutableList<HorarioData> = mutableListOf()
        val db = readableDatabase

        val query = """
        SELECT * FROM Schedules
        INNER JOIN Local ON Schedules.id_local = Local.id
        WHERE Local.id = ?
    """
        val cursor = db.rawQuery(query, arrayOf(campoId.toString()))

        try {
            while (cursor.moveToNext()) {
                val idLocalIndex = cursor.getColumnIndex("id_local")
                val diaIndex = cursor.getColumnIndex("day_of_the_week")
                val inicioIndex = cursor.getColumnIndex("start_time")
                val fimIndex = cursor.getColumnIndex("end_time")
                val checkedIndex = cursor.getColumnIndex("checked")

                if (idLocalIndex >= 0 && diaIndex >= 0 && inicioIndex >= 0 && fimIndex >= 0 && checkedIndex >= 0) {
                    val horarioData = HorarioData(
                        cursor.getInt(idLocalIndex),
                        cursor.getString(diaIndex),
                        cursor.getInt(inicioIndex),
                        cursor.getInt(fimIndex),
                        cursor.getInt(checkedIndex)
                    )
                    horariosList.add(horarioData)
                }
            }
        } finally {
            cursor.close()
        }

        db.close()
        return horariosList
    }

    fun updateCampo(
        campoId: Int,
        userID: Int,
        novoNome: String,
        novoEndereco: String,
        novaDescricao: String,
        novoPrecoHora: Double,
        novoVestiario: String,
        novoEstacionamento: String,
        novoBar: String,
        novosHorarios: ArrayList<HorarioData>
    ): Boolean {
        val db = writableDatabase
        var sucesso = false

        try {
            db.beginTransaction()

            // Atualizar dados na tabela Local
            val valuesLocal = ContentValues().apply {
                put("local_name", novoNome)
                put("address", novoEndereco)
                put("description", novaDescricao)
                put("hourly_rate", novoPrecoHora)
            }
            val resultadoLocal = db.update("Local", valuesLocal, "id = ? AND id_locators = ?", arrayOf(campoId.toString(), userID.toString()))

            // Atualizar dados na tabela Amenities
            val valuesAmenities = ContentValues().apply {
                put("parking", novoVestiario)
                put("locker_room", novoEstacionamento)
                put("pub", novoBar)
            }
            val resultadoAmenities = db.update("Amenities", valuesAmenities, "id = ?", arrayOf(campoId.toString()))

            // Excluir horários antigos para este campo
            db.delete("Schedules", "id_local = ?", arrayOf(campoId.toString()))

            // Adicionar novos horários
            for (horario in novosHorarios) {
                val valuesSchedules = ContentValues().apply {
                    put("id_local", campoId)
                    put("day_of_the_week", horario.day_of_the_week)
                    put("start_time", horario.start_time)
                    put("end_time", horario.end_time)
                    put("checked", horario.cheked)
                }
                db.insert("Schedules", null, valuesSchedules)
            }

            db.setTransactionSuccessful()
            sucesso = true
        } finally {
            db.endTransaction()
            db.close()
        }

        return sucesso
    }

    // Dentro da classe DBHelper
    fun deleteCampo(campoId: Int, userId: Int): Boolean {
        val db = writableDatabase

        // Excluir da tabela Local
        val localResult = db.delete("Local", "id=? AND id_locators=?", arrayOf(campoId.toString(), userId.toString()))

        // Excluir da tabela Amenities
        val amenitiesResult = db.delete("Amenities", "id=?", arrayOf(campoId.toString()))

        // Excluir da tabela Schedules
        val schedulesResult = db.delete("Schedules", "id_local=?", arrayOf(campoId.toString()))

        db.close()

        // Retorna verdadeiro se pelo menos uma linha foi excluída em cada tabela
        return localResult > 0 && amenitiesResult > 0 && schedulesResult > 0
    }

    // No seu DBHelper ou em uma classe auxiliar para manipulação do banco de dados
    fun updateLocador(
        userId: Int,
        novoNome: String,
        novoCnpjCpf: String,
        novoNomeEmpresa: String,
        novoEmail: String,
        novoTelefone: String,
        novaSenha: String,
        novaCidade: String,
        novoEstado: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("fullname", novoNome)
            put("cnpjcpf", novoCnpjCpf)
            put("company_name", novoNomeEmpresa)
            put("email", novoEmail)
            put("phone", novoTelefone)
            put("password", novaSenha)
            put("city", novaCidade)
            put("state", novoEstado)
        }

        val resultado = db.update("Locators", values, "id = ?", arrayOf(userId.toString()))
        db.close()

        return resultado > 0
    }

    fun updateJogador(jogador: Jogador): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("fullname", jogador.fullname)
            put("nickname", jogador.nickname)
            put("password", jogador.password)
            put("city", jogador.city)
            put("state", jogador.state)
        }

        val whereClause = "email = ?"
        val whereArgs = arrayOf(jogador.email)

        return try {
            val rowsAffected = db.update("Users", values, whereClause, whereArgs)
            rowsAffected > 0
        } catch (e: Exception) {
            // Logar a exceção para entender o motivo da falha
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }




    fun deleteLocador(userId: Int): Boolean {
        val db = writableDatabase

        db.beginTransaction()
        try {
            // Exclua o usuário da tabela "Locators"
            val resultadoLocators = db.delete("Locators", "id = ?", arrayOf(userId.toString()))

            // Exclua os locais relacionados a esse usuário na tabela "Local"
            val resultadoLocal = db.delete("Local", "id_locators = ?", arrayOf(userId.toString()))

            // Exclua os horários relacionados a esses locais na tabela "Schedules"
            val resultadoSchedules = db.delete("Schedules", "id_local IN (SELECT id FROM Local WHERE id_locators = ?)", arrayOf(userId.toString()))

            db.setTransactionSuccessful()

            return resultadoLocators > 0 && resultadoLocal >= 0 && resultadoSchedules >= 0
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun deleteUser(userId: Int): Boolean {
        val db = writableDatabase

        db.beginTransaction()
        try {
            // Exclua o usuário da tabela "Users"
            val resultadoUsers = db.delete("Users", "id = ?", arrayOf(userId.toString()))

            db.setTransactionSuccessful()

            return resultadoUsers > 0
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getLocalsByLocatorsCityAndState(city: String, state: String): ArrayList<CampoData> {
        val locals = ArrayList<CampoData>()

        val db = readableDatabase

        val columns = arrayOf(
            "Local.id",
            "Local.id_amenities",
            "Local.id_locators",
            "Local.photo_of_the_place",
            "Local.local_name",
            "Local.address",
            "Local.description",
            "Local.hourly_rate",
            "Amenities.parking",
            "Amenities.locker_room",
            "Amenities.pub",
        )

        val selection = "Locators.city = ? AND Locators.state = ?"
        val selectionArgs = arrayOf(city, state)

        val joinQuery =
            "Local INNER JOIN Locators ON Local.id_locators = Locators.id " +
                    "INNER JOIN Amenities ON Local.id_amenities = Amenities.id"

        val cursor = db.query(joinQuery, columns, selection, selectionArgs, null, null, null)

        try {
            while (cursor.moveToNext()) {
                try {
                    val idIndex = cursor.getColumnIndexOrThrow("id")
                    val id_amenitiesIndex = cursor.getColumnIndexOrThrow("id_amenities")
                    val id_locatorsIndex = cursor.getColumnIndexOrThrow("id_locators")
                    val photoIndex = cursor.getColumnIndexOrThrow("photo_of_the_place")
                    val nameIndex = cursor.getColumnIndexOrThrow("local_name")
                    val addressIndex = cursor.getColumnIndexOrThrow("address")
                    val descriptionIndex = cursor.getColumnIndexOrThrow("description")
                    val hourlyRateIndex = cursor.getColumnIndexOrThrow("hourly_rate")
                    val parkingIndex = cursor.getColumnIndexOrThrow("parking")
                    val lockerRoomIndex = cursor.getColumnIndexOrThrow("locker_room")
                    val pubIndex = cursor.getColumnIndexOrThrow("pub")

                    val local = CampoData(
                        id = cursor.getInt(idIndex),

                        id_locators = cursor.getInt(id_locatorsIndex),
                        photo_of_the_place = cursor.getString(photoIndex) ?: "",
                        local_name = cursor.getString(nameIndex) ?: "",
                        address = cursor.getString(addressIndex) ?: "",
                        description = cursor.getString(descriptionIndex) ?: "",
                        hourly_rate = cursor.getDouble(hourlyRateIndex),
                        parking = cursor.getString(parkingIndex) ?: "",
                        locker_room = cursor.getString(lockerRoomIndex) ?: "",
                        pub = cursor.getString(pubIndex) ?: "",

                    )
                    locals.add(local)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        } finally {
            cursor.close()
            db.close()
        }

        return locals
    }






}
