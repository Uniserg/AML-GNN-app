import flask

from model_exe.prediction import predict_money_launderding

app = flask.Flask(__name__)

@app.route('/api/v1/predict_is_laundering_transation', methods=['POST'])
def predict_is_laundering_transation():
    request_data = flask.request.get_json()
    predict = predict_money_launderding(request_data['transaction_graph'], request_data['transactions_to_predict'])
    return predict

if __name__ == '__main__':
    app.run(debug=True)