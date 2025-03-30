import unittest
import sys
import os
from unittest.mock import patch, MagicMock
import json

sys.modules['dotenv'] = MagicMock()
sys.modules['dotenv'].load_dotenv = MagicMock()
sys.modules['azure.data.tables'] = MagicMock()
sys.modules['azure.core.credentials'] = MagicMock()
sys.modules['azure.core.exceptions'] = MagicMock()

mock_table_client = MagicMock()
sys.modules['azure.data.tables'].TableClient = MagicMock(return_value=mock_table_client)
sys.modules['azure.core.credentials'].AzureNamedKeyCredential = MagicMock()

MockResourceNotFoundError = type('ResourceNotFoundError', (Exception,), {})
sys.modules['azure.core.exceptions'].ResourceNotFoundError = MockResourceNotFoundError

os.environ['COSMOS_DB_URL'] = 'https://mock-cosmos-url.table.cosmos.azure.com:443/'
os.environ['COSMOS_DB_KEY'] = 'mock-cosmos-key'

# Adjust path as necessary for your project structure
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import azure.functions as func
from calc_function.function_app import counter_function


class TestCounterFunctionSimplified(unittest.TestCase):

    def setUp(self):
        mock_table_client.reset_mock()
        mock_table_client.get_entity.side_effect = None
        mock_table_client.get_entity.return_value = None
        mock_table_client.upsert_entity.side_effect = None
        mock_table_client.upsert_entity.return_value = None


    def test_get_existing_count(self):
        mock_request = func.HttpRequest(method="GET", url="/api/counter_function", body=None)
        initial_count = 5
        mock_entity = {"Count": initial_count, "PartitionKey": "visitorCount", "RowKey": "1"}
        mock_table_client.get_entity.return_value = mock_entity

        response = counter_function(mock_request)

        mock_table_client.get_entity.assert_called_once_with(partition_key="visitorCount", row_key="1")
        self.assertEqual(response.status_code, 200)
        body = json.loads(response.get_body().decode('utf-8'))
        self.assertEqual(body, {"count": initial_count})


    def test_post_increments_existing_count(self):
        mock_request = func.HttpRequest(method="POST", url="/api/counter_function", body=None)
        initial_count = 10
        expected_new_count = initial_count + 1
        mock_entity = {"Count": initial_count, "PartitionKey": "visitorCount", "RowKey": "1"}
        mock_table_client.get_entity.return_value = mock_entity

        response = counter_function(mock_request)

        mock_table_client.get_entity.assert_called_once_with(partition_key="visitorCount", row_key="1")
        expected_upsert_payload = {
            "PartitionKey": "visitorCount",
            "RowKey": "1",
            "Count": expected_new_count
        }
        mock_table_client.upsert_entity.assert_called_once_with(expected_upsert_payload)
        self.assertEqual(response.status_code, 200)
        body = json.loads(response.get_body().decode('utf-8'))
        self.assertEqual(body, {"count": expected_new_count})


if __name__ == '__main__':
    unittest.main()