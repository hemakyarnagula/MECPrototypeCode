import numpy as np
from hmmlearn import hmm
import sys

model = hmm.MultinomialHMM(n_components=3, n_iter=100)

model.startprob_ = np.array([3.05583505e-18, 1.40188091e-17, 1.00000000e+00])

model.transmat_ = np.array([[9.00069079e-01, 2.85393283e-04, 9.96455277e-02],
       						[1.13789943e-12, 8.85953834e-01, 1.14046166e-01],
       						[8.11289642e-02, 4.66013153e-02, 8.72269721e-01]])

model.emissionprob_ = np.array([[9.47902807e-01, 5.20971930e-02, 4.47248108e-11, 1.26729203e-36],
       							[3.86971640e-02, 2.94284176e-01, 5.92341074e-01, 7.46775859e-02],
       							[4.48002375e-02, 9.53114531e-01, 2.08523154e-03, 2.05371305e-14]])

obsSeq = map(int, sys.argv[1].strip('[]').split(','))
state_sequence = model.predict(np.array([obsSeq]).T)
prob_next_step = model.transmat_[state_sequence[-1], :]
next_state = np.argmax(prob_next_step)

next_observation = np.argmax(model.emissionprob_[next_state])
# print(next_observation)
print "outing to out\n"