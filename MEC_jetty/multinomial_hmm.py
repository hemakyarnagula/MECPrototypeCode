import numpy as np
from hmmlearn import hmm
import sys

model = hmm.MultinomialHMM(n_components=5, n_iter=100)

#trained values for CAR mobility logs from Aarnes to elverum.
model.startprob_ = np.array([0., 0., 0., 0., 1.])

model.transmat_ = np.array([[9.41648462e-001, 2.78365202e-003, 2.11799951e-002,1.89941672e-022, 3.43878911e-002],
					       [9.66895152e-003, 8.73068493e-001, 4.75644723e-002, 6.96980829e-002, 4.84896506e-023],
					       [1.65303814e-002, 2.34060278e-002, 9.59581300e-001, 3.45769186e-005, 4.47713835e-004],
					       [2.91826658e-039, 7.95727101e-002, 3.21695330e-003, 9.17210337e-001, 4.95450666e-104],
					       [4.22734102e-002, 3.37518862e-004, 6.01926217e-038, 4.93907750e-050, 9.57389071e-001]])

model.emissionprob_ = np.array([[4.04466302e-003, 1.49362071e-001, 4.75629493e-001,
								 3.03179335e-001, 6.77844378e-002, 7.65060420e-013, 7.97591595e-061, 1.62334457e-177],
						       [2.27890952e-001, 7.72109048e-001, 8.33750764e-037,
						        3.37233835e-122, 1.77183701e-222, 3.81563098e-216, 6.56285745e-300, 0.00000000e+000],
						       [1.71577435e-002, 9.64522073e-001, 1.83201831e-002,
						        6.14634023e-069, 3.13258285e-157, 1.26278726e-198, 1.65285754e-242, 0.00000000e+000],
						       [9.61731956e-001, 3.82680442e-002, 3.80088184e-157,
						        2.22329541e-322, 0.00000000e+000, 0.00000000e+000, 0.00000000e+000, 0.00000000e+000],
						       [6.67490191e-027, 1.65660874e-002, 4.33966155e-002,
						        1.69422384e-001, 3.88202763e-001, 3.01170083e-001, 6.48295275e-002, 1.64125386e-002]])

obsSeq = map(int, sys.argv[1].strip('[]').split(','))
state_sequence = model.predict(np.array([obsSeq]).T)
prob_next_step = model.transmat_[state_sequence[-1], :]
next_state = np.argmax(prob_next_step)

next_observation = np.argmax(model.emissionprob_[next_state])
# print(int (next_observation))
if (next_observation==0):
	print (45652)
elif (next_observation==1):
	print(522286)
elif (next_observation==2):
	print(1032682)
elif (next_observation==3):
	print(1546902)
elif (next_observation==4):
	print(2133691)
elif (next_observation==5):
	print(3078587)
elif (next_observation==6):
	print(3840360)
elif (next_observation==7):
	print(4219897)
# print(next_observation)

