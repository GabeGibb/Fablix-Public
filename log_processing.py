
filename = "search_test.txt"
f = open(f"target/cs122b-project1-gg/{filename}", "r")
lines = f.readlines()

tsTotal = 0
tjTotal = 0
count = 0

for line in lines:
    words = line.split()
    tsTotal += int(words[1])
    tjTotal += int(words[3])
    count += 1

tsAvg = (tsTotal / count) / 1000000
tjAvg = (tjTotal / count) / 1000000

print("TS AVERAGE IN MILLISECONDS:", tsAvg)
print("TJ AVERAGE IN MILLISECONDS:", tjAvg)