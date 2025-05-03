/**
 * Source: DBConnections.js (Day 21 Activity A)
 */
const mariadb = require('mariadb');


let pool = null;

exports.getDatabaseConnection = () => {
    if(!pool) {
        pool = mariadb.createPool({
            host: process.env.DB_HOST,
            port: process.env.DB_PORT,
            user: process.env.MYSQL_USER,
            password: process.env.MYSQL_PASSWORD,
            database: process.env.MYSQL_DATABASE,
            charset: process.env.DB_CHARSET
        });
    }

    return pool;
}
exports.executeQuery = (query, params = []) => {
    const pool = module.exports.getDatabaseConnection();

    return pool.query(query, params).catch(err => {
        console.log(err);
        throw err;
    });
}

exports.executeStatement = (query, params = []) => {
    return module.exports.executeQuery(query, params);
}

exports.batch = (query, batchParams = []) => {
    const pool = module.exports.getDatabaseConnection();
    return pool.batch(query, batchParams);
}

exports.close = () => {
    if(pool) {
        pool.end();
        pool = null;
    }
}