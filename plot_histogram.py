import platform
import json
import os

# Import Matplotlib and fix backend for MacOS
import matplotlib
if platform.system() == "Darwin":
	matplotlib.use("TkAgg")
import matplotlib.pyplot as plt


directory = "results"
paths = ["FITNESS_2.json", "LBStatic_2.json"] #, "NOVELTY_1.json", "LBStatic_1.json", "LBDynamic_1.json"]

plt.title("Evolutionary Algorithm Performance (Katsuura)")

plt.xlabel("Score")
plt.xlim(0, 10)

plt.ylabel("Frequency")


for path in paths:
	with open(os.path.join(directory, path)) as json_file:
		json_dictionary = json.load(json_file)
		plt.hist(json_dictionary["output"], label=json_dictionary["name"], bins=40, range=(0, 10))

plt.legend()

# Uncomment the following line to save Histogram to file
plt.savefig("figures/hist_2.png", dpi=300)

# Uncomment the following line to show histogram on screen
plt.show()