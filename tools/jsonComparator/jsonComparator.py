import sys
import json
from deepdiff import DeepDiff

#PROGRAM COMPARES OUTFROM FROM JSON FILE 2 to JSON FILE 1.
#NOTE: The file in Arg 1 is considered the current, and the file in Arg 2 to be the incoming

if len(sys.argv) != 3:
    sys.exit("Two arguments needed: file1.json file2.json")

file1 = open(sys.argv[1])
file2 = open(sys.argv[2])

json1 = json.load(file1)
json2 = json.load(file2)

ddiff = DeepDiff(json1, json2, ignore_order = True)
ddiff.to_json()
print(ddiff)
#write the output of deepdiff to output
# with open('output/comparisonOutput.json', "w", encoding='utf-8') as outputFile:
#     json.dump(ddiff, outputFile, ensure_ascii=False, indent=2)

# outputFile.close()
file1.close()
file2.close()
