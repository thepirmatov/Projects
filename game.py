#Beksultan Pirmatov
#updated and final working version

def wrap(inputString, max_width):
    result = ""
    i = 0
    for i in range(len(inputString)):
        if i % max_width == 0:
            result += "\n"
        result += inputString[i]
    
    return result

def main():
    var = input("Enter sentence: ")
    max_width = int(input("Enter max_width: "))
    while max_width > len(var):
        print("max_width cannot be more than len(sentence)\n")
        max_width = int(input("Enter max_width: "))

    res = wrap(var, max_width)
    print(res)

if __name__ == "__main__":
    main()
    