# NVA Customer API
API for administration of Institutions that are Customers in NVA.

## Deployment considerations

Read this before deploying this template.

### Parameter overrides

Ensure all parameters defined in the Parameter section of [SAM template](template.yaml#L26) are set.

### Resources outside SAM template

These resources are defined outside the SAM template, but used in the template. See details below.

#### Customers DynamoDB Table

Defined by template in 
[cloudformation\dynamodb_customer_table.yml](cloudformation\dynamodb_customer_table.yml) and passed 
to SAM template as parameters with table name and index names. Ensure table and indices is up to 
date before deploying new SAM template.

#### Cognito UserPool

Defined by template in repo [NVA-infrastructure](https://github.com/BIBSYSDEV/NVA-infrastructure) and passed to SAM template as parameter with ARN.