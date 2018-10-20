import subprocess
import json
import re
import os


REGEX = re.compile(r'Score: (\d\.\d+)')
def run_experiment(target: int, parameters: dict):
	return float(re.findall(REGEX, subprocess.check_output(["make", str(target), "FLAGS={}".format(" ".join("-D{}={}".format(p,v) for p,v in parameters.items()))], stderr=subprocess.PIPE).decode())[0])

def run_experiments(target: int, parameters: dict, repetitions: int):
	for i in range(repetitions):
		# print("\rRunning Experiment {:2d}/{:2d}".format(i+1, repetitions), end="")
		result = run_experiment(target, parameters)
		print(result)
		yield result


if __name__ == "__main__":

	## Experiment Settings ##
	directory = "results"
	name = "LBDynamic"
	target = 2
	repetitions = 40

	parameters = {
		'population_size': 208,
		'tournament_size': 2,
		'crossover_points': 2,
		'mutation_probability': 0.35,
		'max_sigma': 1.0971574946931633,
		'learning_rate': 0.23423232947955983,
		'novelty_threshold': 2.736732536773115,
		'linearblend': 0.1,
		'linearblend_delta': 0.09877776913042888,
		'nearestNeighbours': 6
	}

	## Perform Experiment and write to json ##
	if not os.path.exists(directory):
		os.makedirs(directory)

	json_name = os.path.join(directory, "{}_{}.json".format(name, target))
	with open(json_name, 'w') as json_file:

		json_dictionary = {
			"name": name,
			"target": target,
			"parameters": parameters,
			"repetitions": repetitions,
			"output": [experiment for experiment in run_experiments(target, parameters, repetitions)]
		}

		print("\nWriting to {}".format(json_name))
		json.dump(json_dictionary, json_file)

