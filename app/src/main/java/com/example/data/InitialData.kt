package com.example.data

object InitialData {

    val SUBJECTS = listOf(
        Subject(
            id = "BIT301",
            code = "25BIT301",
            name = "Data Structures & Algorithms",
            facultyName = "Dr. K. Senthil Nathan",
            credits = 4
        ),
        Subject(
            id = "BIT302",
            code = "25BIT302",
            name = "Database Management Systems",
            facultyName = "Prof. M. Rajeshwari",
            credits = 4
        ),
        Subject(
            id = "BIT303",
            code = "25BIT303",
            name = "Computer Networks",
            facultyName = "Dr. P. Arumugam",
            credits = 3
        ),
        Subject(
            id = "BIT304",
            code = "25BIT304",
            name = "Operating Systems",
            facultyName = "Prof. V. Sivakumar",
            credits = 3
        ),
        Subject(
            id = "BIT305",
            code = "25BIT305",
            name = "Full Stack Web Technologies",
            facultyName = "Dr. S. Meenakshi",
            credits = 4
        )
    )

    /**
     * Exact 56 student records from the Student Database requirement.
     * All 56 students are marked active with exact roll numbers and names.
     */
    val STUDENTS = listOf(
        Student(rollNumber = "25BIT001", sNo = 1, name = "ABISHEK M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT002", sNo = 2, name = "AJISHRAJ S", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT003", sNo = 3, name = "AKALYA M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT004", sNo = 4, name = "ANAGHA U", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT005", sNo = 5, name = "ARULRAJ I", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT006", sNo = 6, name = "ARUSUDHAN A", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT007", sNo = 7, name = "BALAGANDHARAN S", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT008", sNo = 8, name = "DHARANESH K", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT009", sNo = 9, name = "DHARANI L", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT010", sNo = 10, name = "DHARSIK A", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT011", sNo = 11, name = "DHIVAN K", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT012", sNo = 12, name = "DINESH R", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT013", sNo = 13, name = "ESAKKI RAJE", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT014", sNo = 14, name = "GIRIJEYAM M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT015", sNo = 15, name = "GNANA SEKARAM M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT016", sNo = 16, name = "GOBALA KRISHNAN S", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT017", sNo = 17, name = "GOPIKAN N", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT018", sNo = 18, name = "GOWRISAN B", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT020", sNo = 19, name = "HARIHARAN G", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT021", sNo = 20, name = "HEMAVATHI G", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT022", sNo = 21, name = "JANANI G", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT023", sNo = 22, name = "JEBASTIN", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT024", sNo = 23, name = "JENITA S", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT025", sNo = 24, name = "JOTHIPRIYA P", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT027", sNo = 25, name = "KAVIYARASAN D", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT028", sNo = 26, name = "KISHORE M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT029", sNo = 27, name = "KUTTYMADHESHWARAN M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT030", sNo = 28, name = "MOHAMED ZUBER", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT031", sNo = 29, name = "MOULIDHARAN K", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT032", sNo = 30, name = "NIKISH MOHANRAJ", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT033", sNo = 31, name = "NIRANJANA V", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT034", sNo = 32, name = "NIVANGELLIN M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT035", sNo = 33, name = "PARAMESH P", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT036", sNo = 34, name = "PRAVIN R", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT037", sNo = 35, name = "RAGULAN B", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT038", sNo = 36, name = "RITHIKA R", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT039", sNo = 37, name = "SAKTHI SRI DEVIT", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT040", sNo = 38, name = "SANDHIYA K", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT041", sNo = 39, name = "SANJIV V A", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT042", sNo = 40, name = "SANJITH K", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT043", sNo = 41, name = "SARANYA M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT044", sNo = 42, name = "SARAVANAKUMAR S", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT045", sNo = 43, name = "SARAVANAVEL C", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT046", sNo = 44, name = "SATHYA C", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT047", sNo = 45, name = "SHARAN S", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT048", sNo = 46, name = "SIVARAJ S", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT049", sNo = 47, name = "SUMAN D", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT050", sNo = 48, name = "TAMIL SELVAN N", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT051", sNo = 49, name = "THASHALAN B", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT053", sNo = 50, name = "VETRIVEL K", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT054", sNo = 51, name = "VETRIVEL M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT055", sNo = 52, name = "VISHNU KANTHAN M", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT056", sNo = 53, name = "YAZHINI S", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT057", sNo = 54, name = "YUVASRI S", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT301", sNo = 55, name = "HARI PRASADH G", isActive = true, status = "Active"),
        Student(rollNumber = "25BIT302", sNo = 56, name = "PRADHEESH S", isActive = true, status = "Active")
    )
}
