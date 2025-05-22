import torch
import torch.nn.functional as F
from torch_geometric.nn import GINEConv, TransformerConv, TAGConv
from torch_geometric.data import Data

class AmlGnnClassifier(torch.nn.Module):
    def __init__(self, nodes: torch.tensor, edges: torch.tensor, num_classes):
        super().__init__()

        # GNN-слои для узлов
        self.conv1 = GINEConv(nn=torch.nn.Sequential(
            torch.nn.Linear(nodes.shape[1], 256),
            torch.nn.ReLU(),
            torch.nn.Linear(256, 128)
        ), edge_dim=edges.shape[1])
        self.conv2 =  GINEConv(nn=torch.nn.Sequential(
            torch.nn.Linear(128, 128),
            torch.nn.ReLU(),
            torch.nn.Linear(128, 128)
        ), edge_dim=edges.shape[1])
        self.conv3 =  GINEConv(nn=torch.nn.Linear(128, 64), edge_dim=edges.shape[1])
        self.tag1 = TAGConv(64, 64, K=3)
        self.conv4 =  GINEConv(nn=torch.nn.Linear(64, 128), edge_dim=edges.shape[1])
        self.transformer1 = TransformerConv(128, 128, edge_dim=edges.shape[1])
        self.conv5 =  GINEConv(nn=torch.nn.Linear(128, 64), edge_dim=edges.shape[1])
        self.conv6 =  GINEConv(nn=torch.nn.Linear(64, 128), edge_dim=edges.shape[1])
        self.conv7 =  GINEConv(nn=torch.nn.Linear(128, 256), edge_dim=edges.shape[1])
        self.conv8 =  GINEConv(nn=torch.nn.Linear(256, 256), edge_dim=edges.shape[1])
        self.transformer2 = TransformerConv(256, 256, edge_dim=edges.shape[1])
        self.conv9 =  GINEConv(nn=torch.nn.Linear(256, 512), edge_dim=edges.shape[1])

        # MLP для классификации рёбер
        # Вход: эмбеддинги узлов (source + target)
        self.mlp_classifier = torch.nn.Sequential(
            torch.nn.Linear(512 * 2, 1024),
            torch.nn.ReLU(),
            torch.nn.Linear(1024, 128),
            torch.nn.SELU(),
            torch.nn.Linear(128, num_classes)
        )

    def forward(self, x, edge_index, edge_attr):
        x = F.selu(self.conv1(x, edge_index, edge_attr))
        x = F.selu(self.conv2(x, edge_index, edge_attr))
        x = F.selu(self.conv3(x, edge_index, edge_attr))
        x = F.selu(self.conv4(x, edge_index, edge_attr))
        x = F.selu(self.transformer1(x, edge_index, edge_attr))
        x = F.selu(self.conv5(x, edge_index, edge_attr))
        x = F.selu(self.conv6(x, edge_index, edge_attr))
        x = F.selu(self.conv7(x, edge_index, edge_attr))
        x = F.selu(self.conv8(x, edge_index, edge_attr))
        x = F.selu(self.transformer2(x, edge_index, edge_attr))
        x = F.selu(self.conv9(x, edge_index, edge_attr))


        # Получаем эмбеддинги для source и target узлов каждого ребра
        src, dst = edge_index
        x_src = x[src]  # Эмбеддинги узлов-источников [num_edges, hidden_channels]
        x_dst = x[dst]  # Эмбеддинги узлов-целей [num_edges, hidden_channels]

        # Конкатенируем эмбеддинги узлов и признаки ребра
        edge_features = torch.cat([x_src, x_dst], dim=-1)

        # Предсказываем класс ребра
        return F.softmax(self.mlp_classifier(edge_features))