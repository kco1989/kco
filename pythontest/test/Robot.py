class Robot:
    population = 0

    def __init__(self,name):
        self.name = name
        print("(initialize {0})".format(self.name))
        Robot.population += 1

    def __del__(self):
        print('{0} is being destroyed!'.format(self.name))
        Robot.population -= 1
        if Robot.population == 0:
            print('{0} was the last one.'.format(self.name))
        else:
            print('There are still {0:d} robots working.'.format(Robot.population))

    def sayHi(self):
        """Greeting by the robot.

        Yeah, the can do that."""
        print('Greetings, my master call me {0}'.format(self.name))

    @staticmethod
    def howMany():
        """Prints the current population"""
        print('We have {0:d} robots.'.format(Robot.population))

droid1 = Robot('R2-D2')
droid1.sayHi()
Robot.howMany()

droid2 = Robot('C-3P0')
droid2.sayHi()
Robot.howMany()