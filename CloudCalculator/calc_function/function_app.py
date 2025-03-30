import azure.functions as func
import json
import logging
import os
from dotenv import load_dotenv
from azure.data.tables import TableClient
from azure.core.credentials import AzureNamedKeyCredential

load_dotenv()
cosmos_url = os.environ["COSMOS_DB_URL"]
cosmos_key = os.environ["COSMOS_DB_KEY"]

credential = AzureNamedKeyCredential(name="calc-project-cosmos-db-account", key=cosmos_key)
table_client = TableClient(endpoint=cosmos_url, credential=credential, table_name="VisitorCount")
app = func.FunctionApp()

@app.route(route="counter_function", auth_level=func.AuthLevel.ANONYMOUS, methods=["GET", "POST"])
def counter_function(req: func.HttpRequest) -> func.HttpResponse:
    logging.info(f'Python HTTP trigger function processed a {req.method} request.')
    try:
        partition_key = "visitorCount"
        row_key = "1"
        if req.method == "GET":
            try:
                visitor_item = table_client.get_entity(partition_key=partition_key, row_key=row_key)
                visitor_count = visitor_item.get("Count", 0)
            except Exception:
                visitor_count = 0
            return func.HttpResponse(json.dumps({"count": visitor_count}), mimetype="application/json", status_code=200)
        elif req.method == "POST":
            try:
                visitor_item = table_client.get_entity(partition_key=partition_key, row_key=row_key)
                visitor_count = visitor_item.get("Count", 0)
            except Exception:
                visitor_count = 0
            visitor_count += 1
            table_client.upsert_entity({"PartitionKey": partition_key, "RowKey": row_key, "Count": visitor_count})
            return func.HttpResponse(json.dumps({"count": visitor_count}), mimetype="application/json", status_code=200)
    except Exception as e:
        logging.error(f"Error processing {req.method} request: {str(e)}")
        return func.HttpResponse(json.dumps({"error": "An error occurred"}), mimetype="application/json", status_code=500)