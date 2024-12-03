from flask import Flask, render_template

app = Flask(__name__)

@app.route('/')
def main_page():
    return render_template('MainPage.html')

@app.route('/personal_page')
def personal_page():
    return render_template('personal_page.html')
@app.route('/message')
def message():
    return render_template('message.html')
if __name__ == "__main__":
    app.run(debug=True)
