import pandas as pd
import pickle
from sklearn.preprocessing import OneHotEncoder, RobustScaler
import numpy as np 

def _get_currency_encoder() -> OneHotEncoder:
    with open('encoders/currency_encoder.pkl', 'rb') as f:
        encoder = pickle.load(f)
    return encoder


def _get_payment_format_encoder() -> OneHotEncoder:
    with open('encoders/payment_format_encoder.pkl', 'rb') as f:
        encoder = pickle.load(f)
    return encoder


def _get_amount_scaler() -> RobustScaler:
    with open('scalers/amount_scaler.pkl', 'rb') as f:
        scaler = pickle.load(f)
    return scaler

currency_encoder = _get_currency_encoder()
payment_format_encoder = _get_payment_format_encoder()

amount_scaler = _get_amount_scaler()

def get_prepocessed_nodes(nodes) -> pd.DataFrame:
    currency_one_hot = pd.DataFrame(currency_encoder.transform(nodes[['currency']].rename(columns={"currency": "Currency"})), columns=currency_encoder.categories_[0])
    return pd.concat([nodes.drop(['currency'], axis=1), currency_one_hot], axis=1)

def get_preprocessed_edges_features(edges: pd.DataFrame) -> pd.DataFrame:
    payment_format_one_hot = pd.DataFrame(payment_format_encoder.transform(edges[['paymentType']].rename(columns={"paymentType": "Payment Format"})), columns=payment_format_encoder.categories_[0])
    amount_scaled = amount_scaler.transform(edges['amount'].values.reshape(-1, 1))
    time_scaled = np.log1p(edges['timestamp'].values - edges['timestamp'].min())
    edges = pd.concat([edges.drop(['paymentType', 'amount', 'timestamp'], axis=1), payment_format_one_hot, pd.Series(amount_scaled.reshape(-1), name='amount'), pd.Series(time_scaled, name='timestamp')], axis=1)    
    return edges.drop(["transactionId", "sourceAccount", "targetAccount"], axis=1)
    

def get_account_id_to_idx_map(nodes: pd.DataFrame) -> dict:
    return dict(zip(nodes['accountId'], range(nodes.shape[0])))
