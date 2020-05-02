from flask import Flask, request

"""
This is a completely unsafe, intentionally useless flask app thats sole purpose
is to give our android app something to talk to. I've put no effort into making this safe
and you should not copy any patterns I've used in here for security reasons.
"""

app = Flask(__name__)

AUTH_TOKEN = "587go576g1fvf764vi6fri5v6g7ri5"

@app.route('/')
def root():
    return "Hello, world!"


@app.route('/api/auth', methods=["POST"])
def login():
    body = request.get_json()

    username = body["username"]
    password = body["password"]

    # This is intentionally bad.
    # We just need a "bare-minimum" server to interact with.
    if username == "d0nut" and password == "hunter2":
        # return a pre-defined, hard-coded auth token
        return {"success": True, "data": { "token": AUTH_TOKEN }}, 200
    else:
        # error
        return {"success": False, "data": { "error_message": "Invalid username or password" }}, 401

@app.route('/api/files', methods=["POST"])
def get_files():
    body = request.get_json()

    token = body["token"]

    if token == AUTH_TOKEN:
        files = [ \
            { "name": "selfie.jpeg", "mime": "image/jpeg" }, \
            { "name": "resume.docx", "mime": "application/vnd.openxmlformats-officedocument.wordprocessingml.document" }, \
            { "name": "image01.png", "mime": "image/png" }, \
            { "name": "image02.png", "mime": "image/png" }, \
            { "name": "invoice.pdf", "mime": "application/pdf" }, \
            { "name": "image03.png", "mime": "image/png" }, \
            { "name": "receipt.pdf", "mime": "application/pdf" }, \
            { "name": "image04.png", "mime": "image/png" }, \
            { "name": "image05.png", "mime": "image/png" }, \
            { "name": "image06.png", "mime": "image/png" }, \
            { "name": "image07.png", "mime": "image/png" }, \
            { "name": "presentation.docx", "mime": "application/vnd.openxmlformats-officedocument.wordprocessingml.document" }, \
            { "name": "data.xls", "mime": "application/vnd.ms-excel" }]
        return {"data": files}
    else:
        return {}, 401

if __name__ == "__main__":
    app.run(host='0.0.0.0')
