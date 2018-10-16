import matplotlib.pyplot as plt

def open_scores(file):
    lines = [line.rstrip('\n') for line in open(file)][1:] #remove first line
    score_list = []
    for line in lines:
        if is_number(line):
            score_list.append(float(line))
    return score_list

def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False

def create_plot(scores):
    plt.plot(scores)
    plt.xlabel('Evaluations')
    plt.ylabel('Fitness')
    plt.show()


scores = open_scores('output.txt')
create_plot(scores)
