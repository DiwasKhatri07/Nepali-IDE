package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ProjectEntity::class, CodeFileEntity::class, SnippetEntity::class, FileVersionEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun codeFileDao(): CodeFileDao
    abstract fun snippetDao(): SnippetDao
    abstract fun fileVersionDao(): FileVersionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "code_ide_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val projectDao = db.projectDao()
            val fileDao = db.codeFileDao()
            val snippetDao = db.snippetDao()

            // Default Project 1: Web Workspace
            val webProjId = projectDao.insertProject(
                ProjectEntity(
                    name = "Interactive Web App",
                    description = "HTML, CSS, & JavaScript frontend prototype"
                )
            )

            fileDao.insertFile(
                CodeFileEntity(
                    projectId = webProjId,
                    name = "index.html",
                    path = "index.html",
                    extension = "html",
                    content = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AI Mobile IDE Web Preview</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: #0f111a;
            color: #e2e8f0;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            margin: 0;
            padding: 20px;
            box-sizing: border-box;
        }
        .card {
            background: #1e2238;
            border: 1px solid #2d3250;
            border-radius: 16px;
            padding: 32px;
            max-width: 420px;
            text-align: center;
            box-shadow: 0 10px 25px rgba(0,0,0,0.5);
        }
        h1 {
            color: #818cf8;
            margin-top: 0;
            font-size: 24px;
        }
        p {
            color: #94a3b8;
            line-height: 1.6;
        }
        .btn {
            background: linear-gradient(135deg, #6366f1, #8b5cf6);
            color: white;
            border: none;
            padding: 12px 24px;
            font-size: 16px;
            font-weight: 600;
            border-radius: 8px;
            cursor: pointer;
            transition: transform 0.2s, opacity 0.2s;
            margin-top: 16px;
        }
        .btn:active {
            transform: scale(0.96);
        }
        .counter {
            font-size: 36px;
            font-weight: bold;
            color: #38bdf8;
            margin: 16px 0;
        }
    </style>
</head>
<body>
    <div class="card">
        <h1>Code IDE Touch Preview</h1>
        <p>Edit HTML, CSS, and JS in real-time and see immediate live updates right inside your editor.</p>
        <div class="counter" id="count">0</div>
        <button class="btn" onclick="increment()">Tap Me!</button>
    </div>

    <script>
        let count = 0;
        function increment() {
            count++;
            document.getElementById('count').innerText = count;
            console.log("Button clicked! Current count: " + count);
        }
    </script>
</body>
</html>
                    """.trimIndent()
                )
            )

            fileDao.insertFile(
                CodeFileEntity(
                    projectId = webProjId,
                    name = "styles.css",
                    path = "styles.css",
                    extension = "css",
                    content = """
/* Custom CSS stylesheet */
:root {
    --primary-color: #6366f1;
    --accent-color: #38bdf8;
}

.badge {
    background: var(--primary-color);
    color: white;
    padding: 4px 12px;
    border-radius: 9999px;
    font-size: 12px;
    font-weight: bold;
}
                    """.trimIndent()
                )
            )

            // Default Project 2: Python Script Studio
            val pyProjId = projectDao.insertProject(
                ProjectEntity(
                    name = "Python Algorithms & Data",
                    description = "Python scripts, data processing, and AI prompts"
                )
            )

            fileDao.insertFile(
                CodeFileEntity(
                    projectId = pyProjId,
                    name = "main.py",
                    path = "main.py",
                    extension = "py",
                    content = """
# Python Script Demo - Code IDE
import math
import time

def fibonacci(n):
    "" "Calculates Fibonacci sequence up to n terms" ""
    sequence = [0, 1]
    while len(sequence) < n:
        sequence.append(sequence[-1] + sequence[-2])
    return sequence

def analyze_dataset(data):
    total = sum(data)
    avg = total / len(data)
    variance = sum((x - avg) ** 2 for x in data) / len(data)
    std_dev = math.sqrt(variance)
    return {
        "count": len(data),
        "total": total,
        "average": round(avg, 2),
        "std_dev": round(std_dev, 2)
    }

print("=== Code IDE Python Interpreter ===")
print("Generating Fibonacci terms...")
fib_series = fibonacci(10)
print(f"Fibonacci (10): {fib_series}")

sample_scores = [88, 92, 79, 95, 100, 84, 91]
stats = analyze_dataset(sample_scores)
print("\nDataset Statistics:")
for key, value in stats.items():
    print(f"  • {key.capitalize()}: {value}")

print("\nExecution completed successfully! ✨")
                    """.trimIndent()
                )
            )

            fileDao.insertFile(
                CodeFileEntity(
                    projectId = pyProjId,
                    name = "utils.py",
                    path = "utils.py",
                    extension = "py",
                    content = """
# Utility functions
def format_currency(amount, currency="$"):
    return f"{currency}{amount:,.2f}"

def greet_user(name):
    return f"Hello, {name}! Welcome to Mobile Code IDE."
                    """.trimIndent()
                )
            )

            // Pre-built Snippets
            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "HTML5 Boilerplate",
                    language = "html",
                    prefix = "doc",
                    code = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <title>Document</title>\n</head>\n<body>\n    \n</body>\n</html>",
                    description = "Standard HTML5 structure template"
                )
            )

            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "Python Main Guard",
                    language = "py",
                    prefix = "ifmain",
                    code = "if __name__ == '__main__':\n    main()",
                    description = "Python standard script entry point"
                )
            )

            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "Python Class Template",
                    language = "py",
                    prefix = "class",
                    code = "class MyClass:\n    def __init__(self, name):\n        self.name = name\n\n    def display(self):\n        print(f\"Hello {self.name}\")",
                    description = "Class declaration snippet"
                )
            )

            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "CSS Flexbox Center",
                    language = "css",
                    prefix = "flexcenter",
                    code = "display: flex;\nalign-items: center;\njustify-content: center;",
                    description = "Centering elements with CSS flexbox"
                )
            )
        }
    }
}
