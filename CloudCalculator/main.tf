terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
  }
}

# Use shell command to export subscription id and Azure CLI for authentication
provider "azurerm" {
  features {}
  subscription_id = "cd1e0d57-07f3-4898-a92c-073598f2d6ad"
}

# Resource group for organizing resources
resource "azurerm_resource_group" "main_gr" {
  name     = "main-resources"
  location = "East US 2"
}

# Storage account for calc project
resource "azurerm_storage_account" "storage_acc" {
  name                     = "calculatorstorageacc1"
  resource_group_name      = azurerm_resource_group.main_gr.name
  location                 = azurerm_resource_group.main_gr.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

# Storage container to hold blob storage
resource "azurerm_storage_container" "storage_con" {
  name                  = "$web"
  storage_account_id    = azurerm_storage_account.storage_acc.id
  container_access_type = "blob"
  depends_on = [azurerm_storage_account.storage_acc]
}

# A Blob storage for each static files
resource "azurerm_storage_blob" "storage_blob1" {
  name                   = "index.html"
  storage_account_name   = azurerm_storage_account.storage_acc.name
  storage_container_name = azurerm_storage_container.storage_con.name
  type                   = "Block"
  content_type           = "text/html"
  source                 = "./storage-files/index.html"
}

resource "azurerm_storage_blob" "storage_blob2" {
  name                   = "styles.css"
  storage_account_name   = azurerm_storage_account.storage_acc.name
  storage_container_name = azurerm_storage_container.storage_con.name
  type                   = "Block"
  content_type           = "text/css"
  source                 = "./storage-files/styles.css"
}

resource "azurerm_storage_blob" "storage_blob3" {
  name                   = "script.js"
  storage_account_name   = azurerm_storage_account.storage_acc.name
  storage_container_name = azurerm_storage_container.storage_con.name
  type                   = "Block"
  content_type           = "application/javascript"
  source                 = "./storage-files/script.js"
}

resource "azurerm_storage_blob" "storage_blob4" {
  name                   = "visitor.js"
  storage_account_name   = azurerm_storage_account.storage_acc.name
  storage_container_name = azurerm_storage_container.storage_con.name
  type                   = "Block"
  content_type           = "application/javascript"
  source                 = "./storage-files/visitor.js"
}

# Enable azure static website hosting
resource "azurerm_storage_account_static_website" "static_website" {
  storage_account_id = azurerm_storage_account.storage_acc.id
  index_document     = "index.html"
}

# Azure CDN
resource "azurerm_cdn_profile" "cdn_profile" {
  name                = "cdn-profile"
  location            = azurerm_resource_group.main_gr.location
  resource_group_name = azurerm_resource_group.main_gr.name
  sku                 = "Standard_Microsoft"
}

resource "azurerm_cdn_endpoint" "cdn_endpoint" {
  name                = "huycalcproject-cdn-endpoint"
  profile_name        = azurerm_cdn_profile.cdn_profile.name
  location            = azurerm_resource_group.main_gr.location
  resource_group_name = azurerm_resource_group.main_gr.name

  origin {
    name      = "storage-origin"
    host_name = azurerm_storage_account.storage_acc.primary_web_host 
  }
}

# DNS zone for project domain
resource "azurerm_dns_zone" "my_zone" {
  name                = "huycalcproject.xyz"
  resource_group_name = azurerm_resource_group.main_gr.name
}

resource "azurerm_dns_cname_record" "www_cdn" {
  name                = "www"
  zone_name           = azurerm_dns_zone.my_zone.name
  resource_group_name = azurerm_resource_group.main_gr.name
  ttl                 = 300
  target_resource_id  = azurerm_cdn_endpoint.cdn_endpoint.id 
}

# Create Cosmos DB account with Table API
resource "azurerm_cosmosdb_account" "cosmosdb_account" {
  name                = "calc-project-cosmos-db-account"
  location            = azurerm_resource_group.main_gr.location
  resource_group_name = azurerm_resource_group.main_gr.name
  offer_type          = "Standard"
  kind                = "GlobalDocumentDB"

  automatic_failover_enabled = true

  capabilities {
    name = "EnableTable"
  }

  capabilities {
    name = "EnableServerless"
  }

  consistency_policy {
    consistency_level = "Eventual"
  }

  geo_location {
    location          = "westus"
    failover_priority = 0
  }
}

# Create a Cosmos DB Table (VisitorCount table)
resource "azurerm_cosmosdb_table" "cosmosdb_table" {
  name                = "VisitorCount"
  resource_group_name = azurerm_resource_group.main_gr.name
  account_name        = azurerm_cosmosdb_account.cosmosdb_account.name
}

resource "azurerm_service_plan" "function_plan" {
  name                = "calc-function-service-plan"
  resource_group_name = azurerm_resource_group.main_gr.name
  location            = azurerm_resource_group.main_gr.location
  os_type             = "Linux"
  sku_name            = "Y1"
}

resource "azurerm_linux_function_app" "my_function_app" {
  name                = "calc-linux-function-app"
  resource_group_name = azurerm_resource_group.main_gr.name
  location            = azurerm_resource_group.main_gr.location

  storage_account_name       = azurerm_storage_account.storage_acc.name
  storage_account_access_key = azurerm_storage_account.storage_acc.primary_access_key
  service_plan_id            = azurerm_service_plan.function_plan.id

  site_config {
    application_stack {
      python_version = "3.12"
    }
  }

  app_settings = {
    FUNCTIONS_WORKER_RUNTIME = "python"
    WEBSITE_RUN_FROM_PACKAGE = "1"
    COSMOS_DB_URL            = format("https://%s.table.cosmos.azure.com:443/", azurerm_cosmosdb_account.cosmosdb_account.name)
    COSMOS_DB_KEY            = azurerm_cosmosdb_account.cosmosdb_account.primary_key
  }
}

