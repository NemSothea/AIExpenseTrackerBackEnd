-- 1. Get all expenses with user and category info:
SELECT 
    e.id,
    u.username,
    c.name as category,
    e.amount,
    e.description,
    e.expense_date,
    e.created_at
FROM expenses e
JOIN users u ON e.user_id = u.id
JOIN categories c ON e.category_id = c.id
WHERE e.isactive = true
ORDER BY e.expense_date DESC;

-- 2. Get total expenses by user:

SELECT 
    u.username,
    COUNT(e.id) as total_expenses,
    SUM(e.amount) as total_amount
FROM users u
LEFT JOIN expenses e ON u.id = e.user_id AND e.isactive = true
WHERE u.isactive = true
GROUP BY u.id, u.username
ORDER BY total_amount DESC;

-- 3. Get expenses by category:
SELECT 
    c.name as category,
    COUNT(e.id) as expense_count,
    SUM(e.amount) as total_amount
FROM categories c
LEFT JOIN expenses e ON c.id = e.category_id AND e.isactive = true
WHERE c.isactive = true
GROUP BY c.id, c.name
ORDER BY total_amount DESC;