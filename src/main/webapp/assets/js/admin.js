// Custom JavaScript for Admin Dashboard
document.addEventListener('DOMContentLoaded', function() {
    // Confirmation for Delete Actions
    const deleteButtons = document.querySelectorAll('form[action="AdminServlet"] button.btn-danger');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const form = this.closest('form');
            const action = form.querySelector('input[name="action"]').value;

            if (action === 'deleteUser' || action === 'cancelBooking') {
                e.preventDefault();
                const confirmMsg = action === 'deleteUser'
                    ? 'Are you sure you want to delete this user?'
                    : 'Are you sure you want to cancel this booking?';

                if (confirm(confirmMsg)) {
                    form.submit();
                }
            }
        });
    });

    // Status Change Handler
    const statusForms = document.querySelectorAll('form[action="AdminSupportServlet"]');
    statusForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const select = this.querySelector('select[name="status"]');
            const currentStatus = select.options[select.selectedIndex].text;
            if (currentStatus === 'Closed') {
                if (!confirm('Closing this ticket will mark it as complete. Proceed?')) {
                    e.preventDefault();
                }
            }
        });
    });

    // Table Row Hover Effect
    const tableRows = document.querySelectorAll('.table tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#eef2f7';
            this.style.transition = 'background-color 0.3s';
        });
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });

    // Auto-refresh reports every 30 seconds (optional)
    if (document.querySelector('#reports.show.active')) {
        setInterval(() => {
            window.location.href = 'AdminServlet?action=generateReports';
        }, 30000);
    }
});