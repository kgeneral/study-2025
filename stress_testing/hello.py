from locust import HttpUser, task, TaskSet, FastHttpUser
from locust.clients import HttpSession
from locust import HttpUser, task, between
from locust.env import Environment
from locust.stats import stats_printer
from locust.log import setup_logging
import gevent

class HelloTasks(TaskSet):
    @task
    def hello(self):
        # Make the POST request to the given API endpoint
        self.client.get(url="/")


class ApiUser(FastHttpUser):
    tasks = [HelloTasks]
    wait_time = between(0.1, 0.2)
    host = "http://localhost:8080"


def run_locust_test():
    # Set up logging
    setup_logging("INFO", None)

    # Create an environment and assign the user class
    env = Environment(user_classes=[ApiUser])
    env.create_local_runner()

    # Start a greenlet that periodically outputs the current stats
    gevent.spawn(stats_printer(env.stats))

    # Start the test
    env.runner.start(user_count=1, spawn_rate=1)

    # Run the test for 10 seconds
    gevent.sleep(10)

    # Stop the test
    env.runner.stop()

    # Ensure all data is processed
    env.events.quitting.fire()

if __name__ == "__main__":
    run_locust_test()