import platform
import json
import os

# Import Matplotlib and fix backend for MacOS
import matplotlib
if platform.system() == "Darwin":
	matplotlib.use("TkAgg")
import matplotlib.pyplot as plt


directory = "results"
paths = ["FITNESS_3.json", "NOVELTY_3.json", "LBStatic_3.json", "LBDynamic_3.json"]

plt.title("Evolutionary Algorithm Performance")
plt.xlabel("Score")
plt.ylabel("Frequency")

for path in paths:
	with open(os.path.join(directory, path)) as json_file:
		json_dictionary = json.load(json_file)
		plt.hist(json_dictionary["output"], label=json_dictionary["name"])

plt.legend()

# Uncomment the following line to save Histogram to file
# plt.savefig("figures/hist_3.png", dpi=300)

# Uncomment the following line to show histogram on screen
plt.show()