import subprocess, platform, os, zipfile, shutil

# Windows treated seperatly from Linux
IS_WINDOWS = platform.system() == 'Windows'

process = subprocess.Popen(
    ['gradlew', 'clean', 'build'],
    cwd='../',
    shell=IS_WINDOWS,
    creationflags=subprocess.CREATE_NEW_PROCESS_GROUP)
process.communicate()

for file in os.listdir("../build/distributions"):
    if file.endswith(".zip") and "jvm" in file:
        print("Found zip {0}".format(file))
        fileName = os.path.splitext(file)[0]
        with zipfile.ZipFile("../build/distributions/{0}".format(file)) as data:
            data.extractall("../build/distributions/")
        shutil.move("../build/distributions/{0}".format(fileName), "../build/output/")
