import Dexie from 'dexie';

export const db = new Dexie('FitnessLocalDB');
db.version(1).stores({
    user: '++usr_id, &usr_username, &usr_email',
    activity: 'name, is_timed',
    exercise_plan: '[title+author_id], author_id',
    user_exercise_plan: '[user_id+plan_title], user_id, plan_title, plan_author_id',
    plan_activity: '[plan_title+activity_name], plan_title, plan_author_id, activity_name, progress_estimate, num_sets',
    active_session: '[plan_title+start_timestamp+user_id], plan_title, user_id, end_timestamp',
    session_log: '[plan_title+session_start_timestamp+user_id+activity_name+activity_set], plan_title, session_start_timestamp, user_id, activity_name, activity_set, activity_start_timestamp, activity_end_timestamp, progress',
    session_log_metrics: '[plan_title+session_start_timestamp+user_id+activity_name+activity_set+metric], plan_title, session_start_timestamp, user_id, activity_name, activity_set, metric, value',
    offline_queue: '++id, operation, endpoint, data, timestamp'
});

const isValidTable = (tableName) => {
    const exists = db.table(tableName) !== undefined;
    if (!exists) {
        console.error(`'${tableName}' does not exist in the schema.`);
    }
    return exists;
};

export async function addItemOffline(tableName, itemData) {
    if (!isValidTable(tableName)) return Promise.reject(new Error(`Invalid table name: ${tableName}`));
    try {
        const key = await db[tableName].add(itemData);
        console.log(`Item added to '${tableName}' with key:`, key);
        return key;
    } catch (error) {
        handleError('addItem', tableName, error);
    }
}

export async function editItemOffline(tableName, itemData) {
    if (!isValidTable(tableName)) return Promise.reject(new Error(`Invalid table name: ${tableName}`));
    try {
        const key = await db[tableName].put(itemData);
        console.log(`Item updated in '${tableName}' with key:`, key);
        return key;
    } catch (error) {
        handleError('editItem', tableName, error);
    }
}