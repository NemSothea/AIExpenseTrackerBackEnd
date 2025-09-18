-- 1. Get all expenses with user and category info:
SELECT 
    e.id,
    u.name,
    c.name as category,
    e.amount,
    e.description,
    e.expense_date,
    e.created_at
FROM expenses e
JOIN users u ON e.user_id = u.id
JOIN categories c ON e.category_id = c.id
WHERE e.enabled = true
ORDER BY e.expense_date DESC;

-- 2. Get total expenses by user:

SELECT 
    u.name,
    COUNT(e.id) as total_expenses,
    SUM(e.amount) as total_amount
FROM users u
LEFT JOIN expenses e ON u.id = e.user_id AND e.enabled = true
WHERE u.enabled = true
GROUP BY u.id, u.name
ORDER BY total_amount DESC;

-- 3. Get expenses by category:
SELECT 
    c.name as category,
    COUNT(e.id) as expense_count,
    SUM(e.amount) as total_amount
FROM categories c
LEFT JOIN expenses e ON c.id = e.category_id AND e.enabled = true
WHERE c.enabled = true
GROUP BY c.id, c.name
ORDER BY total_amount DESC;