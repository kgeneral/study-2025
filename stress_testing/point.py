import random

from locust import HttpUser, task


class ApiUser(HttpUser):
    @task
    def post_user_point(self):
        # Make the POST request to the given API endpoint
        self.client.post(
            url="/user/1/point",
            headers={
                "Content-Type": "application/json"
            },
            json={
                "point": random.randint(1, 10)
            }
        )
