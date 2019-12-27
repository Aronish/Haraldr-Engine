import re

def readFile():
    fileName = input("Enter file name: ")
    fileData = []
    file = open(fileName, "r")
    fileData = file.readlines()
    file.close()
    return fileData

def writeFile(dataToWrite):
    fileName = input("Enter file name: ")
    file = open(fileName, "w")
    file.write(dataToWrite)
    file.close()

def processData(keyData):
    processedData = ""
    for entry in keyData:
        if entry[0] == "#":
            processedData += "//" + entry[1:]
        else:
            strippedData = re.sub("=([0-9])*,", "", "".join(entry.split()))
            processedData += strippedData[5:] + "\t\t\t(GLFW." + strippedData + "),\n"
            
    return processedData

def main():
    keyData = readFile()
    processedData = processData(keyData)
    print(processedData)
    writeFile(processedData)
    
if __name__ == "__main__":
    main()
