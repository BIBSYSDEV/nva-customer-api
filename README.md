# NVA Customer API
API for administration of Institutions that are Customers in NVA.

## Deploy considerations

Read this before deploying this template.

### Resources outside SAM template

These resources are defined outside the SAM template but used in the template.

#### Customers DynamoDB Table

Defined by template in 
[cloudformation\dynamodb_customer_table.yml](cloudformation\dynamodb_customer_table.yml) and passed 
to SAM template as parameters with table name and index names. Ensure table and indices is up to 
date before deploying new SAM template.

#### Cognito UserPool

Defined by template in repo [NVA-infrastructure](https://github.com/BIBSYSDEV/NVA-infrastructure) and passed to SAM template as parameter with ARN.