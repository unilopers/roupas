import uuid
import random
import string

from locust import HttpUser, task, between, events


# ──────────────────────────────────────────────
#  Configuração
# ──────────────────────────────────────────────
LOGIN_EMAIL = "admin@louisvittao.com"
LOGIN_PASSWORD = "admin123"

AUTH_XML = f"""<authRequest>
    <email>{LOGIN_EMAIL}</email>
    <password>{LOGIN_PASSWORD}</password>
</authRequest>"""

HEADERS_XML = {"Content-Type": "application/xml"}


def _random_string(length=8):
    return "".join(random.choices(string.ascii_lowercase, k=length))


def _random_phone():
    return f"({random.randint(10,99)}) {random.randint(90000,99999)}-{random.randint(1000,9999)}"


def _build_user_xml():
    name = f"User_{_random_string(6)}"
    email = f"{_random_string(8)}@test.com"
    phone = _random_phone()
    address = f"Rua {_random_string(10)}, {random.randint(1, 999)}"
    password = _random_string(12)
    return f"""<User>
    <name>{name}</name>
    <email>{email}</email>
    <phone>{phone}</phone>
    <address>{address}</address>
    <role>USER</role>
    <password>{password}</password>
</User>"""

def _build_product_xml():
    name = f"Produto_{_random_string(6)}"
    category = random.choice(["Camiseta", "Calca", "Vestido", "Tenis", "Acessorio"])
    color = random.choice(["Preto", "Branco", "Azul", "Vermelho", "Verde"])
    size = random.choice(["PP", "P", "M", "G", "GG"])
    price = round(random.uniform(19.9, 399.9), 2)
    active = random.choice([True, False])
    return f"""<product>
    <name>{name}</name>
    <category>{category}</category>
    <color>{color}</color>
    <size>{size}</size>
    <price>{price}</price>
    <active>{str(active).lower()}</active>
</product>"""



class AuthenticatedUser(HttpUser):
    wait_time = between(1, 3)
    host = "http://localhost:8080"
    token = None

    def on_start(self):
        """Obtém token JWT via /auth/login antes de iniciar os testes."""
        response = self.client.post(
            "/auth/login",
            data=AUTH_XML,
            headers=HEADERS_XML,
            name="/auth/login",
        )
        if response.status_code == 200:
            import xml.etree.ElementTree as ET
            root = ET.fromstring(response.text)
            self.token = root.findtext("token")

    def _auth_headers(self):
        headers = {**HEADERS_XML}
        if self.token:
            headers["Authorization"] = f"Bearer {self.token}"
        return headers


# ──────────────────────────────────────────────
#  Locust Users
# ──────────────────────────────────────────────
class UserLoadTest(AuthenticatedUser):
    created_user_ids = []

    # ── POST /api/user/create ──
    @task(1)
    def create_user(self):
        xml_body = _build_user_xml()
        with self.client.post(
            "/api/user/create",
            data=xml_body,
            headers=self._auth_headers(),
            name="POST /api/user/create",
            catch_response=True,
        ) as response:
            if response.status_code == 201:
                import xml.etree.ElementTree as ET
                root = ET.fromstring(response.text)
                user_id = root.findtext("userId")
                if user_id:
                    self.created_user_ids.append(user_id)
                response.success()
            else:
                response.failure(f"Status {response.status_code}")

    # ── GET /api/user/all ──
    @task(3)
    def get_all_users(self):
        self.client.get(
            "/api/user/all",
            headers=self._auth_headers(),
            name="GET /api/user/all",
        )

    # ── GET /api/user/{id} ──
    @task(2)
    def get_user_by_id(self):
        if not self.created_user_ids:
            return
        user_id = random.choice(self.created_user_ids)
        self.client.get(
            f"/api/user/{user_id}",
            headers=self._auth_headers(),
            name="GET /api/user/{id}",
        )

# ──────────────────────────────────────────────
#  Locust Products
# ──────────────────────────────────────────────
class ProductLoadTest(AuthenticatedUser):
    created_product_ids = []

    # ── POST /api/product/create ──
    @task(1)
    def create_product(self):
        xml_body = _build_product_xml()
        with self.client.post(
            "/api/product/create",
            data=xml_body,
            headers=self._auth_headers(),
            name="POST /api/product/create",
            catch_response=True,
        ) as response:
            if response.status_code == 201:
                import xml.etree.ElementTree as ET
                root = ET.fromstring(response.text)
                product_id = root.findtext("productId") or root.findtext(".//productId")
                if product_id:
                    self.created_product_ids.append(product_id)
                response.success()
            else:
                response.failure(f"Status {response.status_code}")

    # ── GET /api/product/all ──
    @task(3)
    def get_all_products(self):
        self.client.get(
            "/api/product/all",
            headers=self._auth_headers(),
            name="GET /api/product/all",
        )

    # ── GET /api/product/{id} ──
    @task(2)
    def get_product_by_id(self):
        if not self.created_product_ids:
            return
        product_id = random.choice(self.created_product_ids)
        self.client.get(
            f"/api/product/{product_id}",
            headers=self._auth_headers(),
            name="GET /api/product/{id}",
        )

    # ── PUT /api/product/update/{id} ──
    @task(1)
    def update_product(self):
        if not self.created_product_ids:
            return
        product_id = random.choice(self.created_product_ids)
        xml_body = _build_product_xml()
        with self.client.put(
            f"/api/product/update/{product_id}",
            data=xml_body,
            headers=self._auth_headers(),
            name="PUT /api/product/update/{id}",
            catch_response=True,
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status {response.status_code}")

    # ── DELETE /api/product/delete/{id} ──
    @task(1)
    def delete_product(self):
        if not self.created_product_ids:
            return
        product_id = random.choice(self.created_product_ids)
        with self.client.delete(
            f"/api/product/delete/{product_id}",
            headers=self._auth_headers(),
            name="DELETE /api/product/delete/{id}",
            catch_response=True,
        ) as response:
            if response.status_code == 204:
                try:
                    self.created_product_ids.remove(product_id)
                except ValueError:
                    pass
                response.success()
            else:
                response.failure(f"Status {response.status_code}")
