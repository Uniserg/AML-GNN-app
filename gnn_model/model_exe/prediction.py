import pandas as pd
from preprocessing import preprocessing
from torch_geometric.data import Data
import torch
from model_acrh.aml_gnn_classifier import AmlGnnClassifier

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

aml_gnn_classifier = AmlGnnClassifier(n_nodes_features=17, n_edges_features=9, num_classes=2)

checkpoint = torch.load('model_bin/model2-1.pth', map_location=torch.device('cpu'))
aml_gnn_classifier.load_state_dict(checkpoint['model_state_dict'])
aml_gnn_classifier.eval()


def predict_money_launderding(transaction_graph, transactions_to_predict) -> torch.Tensor:
    nodes = pd.DataFrame(transaction_graph['nodes'])
    edges = pd.DataFrame(transaction_graph['edges'])

    node_features = torch.tensor(preprocessing.get_prepocessed_nodes(nodes).values)

    edge_attrs = torch.tensor(preprocessing.get_preprocessed_edges_features(edges).values)

    account_id_to_idx_map = preprocessing.get_account_id_to_idx_map(nodes)

    edge_id_to_idx_map = dict(zip(edges['transactionId'], range(edges.shape[0])))

    src = edges['sourceAccount'].apply(lambda x: account_id_to_idx_map[x]).values
    dest = edges['targetAccount'].apply(lambda x: account_id_to_idx_map[x]).values

    edge_index = torch.tensor([src, dest], dtype=torch.long)

    graph = Data(
        x=node_features.float(),
        edge_index=edge_index,
        edge_attr=edge_attrs)
    
    graph.x = graph.x.to(device).float()
    # graph.y = graph.y.to(device).float()
    graph.edge_attr = graph.edge_attr.to(device).float()
    graph.edge_index = graph.edge_index.to(device)

    with torch.no_grad():
        pred = aml_gnn_classifier(x=graph.x, 
                        edge_index=graph.edge_index, 
                        edge_attr=graph.edge_attr)
        
        idx_to_predict = list(map(lambda x: edge_id_to_idx_map[x], transactions_to_predict)) 
        return pred[idx_to_predict, 1].tolist()
