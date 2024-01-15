def getBitCode(val):
    if val=="1":
        return 1
    elif val=="0":
        return -1
    else:
        return 0
    
#considering ranging between -4 to 4
def getDecimal(val):
    num=val[1:]
    deci=int(num,2)
    if(val[0]=="1"):
        deci=-deci
    return deci

#considering ranging between -4 to 4
def getBinary(num):
    sign="0"
    val=""
    if(num<0):
        sign="1"
        num=abs(num)
    for i in range(0,3):
        if(num & (1<<i)):
            val="1"+val
        else:
            val="0"+val
    val=sign+val
    return val

if __name__=="__main__":
    # print(getBinary(-2))
    # print(getDecimal("0010"))
    # arr=[[1,2,3],[2,3,4],[3,4,5]]
    # print(arr)
    # for i in range(3):
    #     for j in range(3):
    #         print(arr[i][j])
    i=0
    open(f"receive{i}.txt",'w').write("tonmouy")