from skopt.space import Real, Integer
from skopt.utils import use_named_args
from skopt import gp_minimize

import subprocess
import sys
import re


REGEX = re.compile(r'Score: (\d\.\d+)')
if len(sys.argv) == 2:
	TARGET = int(sys.argv[1])
else:
	print("usage: python tune.py <1/2/3>")
	exit()


space = [
	Integer(50, 250, name="population_size"),
	Integer(2, 6, name="tournament_size"),
	Real(0.1, 0.35, name="mutation_probability"),
	Real(1, 2, name="max_sigma"),
	Real(0, 10, name="novelty_threshold"),
	Real(0, 1, name="linearblend"),
	Real(0, 0.1, name="linearblend_delta"),
	Integer(2, 6, name="nearestNeighbours")
]

@use_named_args(space)
def evaluate(**parameters: dict):
	score = -float(re.findall(REGEX, subprocess.check_output(["make", str(TARGET), "FLAGS={}".format(" ".join("-D{}={}".format(p,v) for p,v in parameters.items()))], stderr=subprocess.PIPE).decode())[0])
	print("{}\n{}\n".format(parameters, score))
	return score

result = gp_minimize(evaluate, space, n_calls=100, random_state=0)

print("Best Score: {}".format(result.fun))
print("Best Parameters: ")

for i, param in enumerate(["population_size", "tournament_size", "mutation_probability", "max_sigma", "novelty_threshold", "linearblend", "linearblend_delta", "nearestNeighbours"]):
	print("\t{:30s}: {}".format(param, result.x[i]))