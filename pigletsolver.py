GOAL = 10
EPSILON = 1e-9

p = [[[0 for k in range(GOAL)] for j in range(GOAL)] for i in range(GOAL)]
flip = [[[False for k in range(GOAL)] for j in range(GOAL)] for i in range(GOAL)]

def valueIterate():
    maxChange = EPSILON # Starting here to make sure we enter loop
    while maxChange >= EPSILON:
        maxChange = 0.0
        for i in range(GOAL): # i = PLAYER SCORE
            for j in range(GOAL): # j = OPPONENT SCORE
                for k in range(GOAL-i): # k = TURN TOTAL SO FAR
                    oldProb = p[i][j][k]
                    pFlip = (1.0 - pWin(j,i,0) + pWin(i,j,k+1)) / 2
                    pHold = 1.0 - pWin(j, i+k, 0)
                    p[i][j][k] = max(pFlip, pHold)
                    flip[i][j][k] = pFlip > pHold
                    change = abs(p[i][j][k] - oldProb)
                    maxChange = max(maxChange, change)

def pWin(i, j, k):
    if i+k >= GOAL:
        return 1.0
    elif j >= GOAL:
        return 0.0
    else:
        return p[i][j][k]

def outputHoldValues():
    for i in range(GOAL):
        for j in range(GOAL):
            k = 0
            while k < GOAL - i and flip[i][j][k]:
                k += 1
            print(k,end=' ')
        print()

def main():
    valueIterate()
    outputHoldValues()

if __name__ == '__main__':
    main()