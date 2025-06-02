class Transfer:
    def __init__(self, source_account, target_account, timestamp, amount, payment_type) -> None:
        self.source_account = source_account
        self.target_account = target_account
        self.timestamp = timestamp
        self.amount = amount
        self.payment_type = payment_type

    @classmethod
    def from_json(cls, json_data):
        return cls(
            source_account=json_data['sourceAccount'],
            target_account=json_data['targetAccount'],
            timestamp=json_data['timestamp'], 
            amount=json_data['amount'], 
            payment_type=['paymentType'])