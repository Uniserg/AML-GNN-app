from .account import Account
from .transfer import Transfer

class TransactionGraph:
    def __init__(self, nodes: list[Account], edges: list[Transfer]):
        self.nodes: list[Account] = nodes
        self.edges = edges
    
    @classmethod
    def from_json(cls, json_data: list[dict]):
        nodes = list(map(lambda x: Account.from_json(x), json_data['nodes']))
        edges = list(map(lambda x: Transfer.from_json(x), json_data['edges']))
        return cls(nodes, edges)