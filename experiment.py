import subprocess
import json
import re
import os


REGEX = re.compile(r'Score: (\d\.\d+)')
def run_experiment(target: int, parameters: dict):
	return float(re.findall(REGEX, subprocess.check_output(["make", str(target), "FLAGS={}".format(" ".join("-D{}={}".format(p,v) for p,v in parameters.items()))], stderr=subprocess.PIPE).decode())[0])

def run_experiments(target: int, parameters: dict, repetitions: int):
	for i in range(repetitions):
		print("\rRunning Experiment {:2d}/{:2d}".format(i+1, repetitions), end="")
		yield run_experiment(target, parameters)


if __name__ == "__main__":

	## Experiment Settings ##
	directory = "results"
	name = "NOVELTY"
	target = 3
	repetitions = 40

	parameters = {
		'population_size': 250,
		'tournament_size': 2,
		'crossover_points': 5,
		'mutation_probability': 0.35,
		'max_sigma': 2.0,
		'learning_rate': 0.19145968897006707,
		'novelty_threshold': 10.0,
		'linearblend': 0.0,
		'linearblend_delta': 0.0,
		'nearestNeighbours': 6,
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

