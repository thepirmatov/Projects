import pytest
from app import app, db, User

@pytest.fixture
def test_app():
    app.config['TESTING'] = True
    app.config['WTF_CSRF_ENABLED'] = False
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///test.db'

    with app.app_context():
        db.create_all()

        # Create a test user
        test_user = User(username='testuser', password='testpassword')
        db.session.add(test_user)
        db.session.commit()

        yield app

        db.session.remove()
        db.drop_all()

@pytest.fixture
def client(test_app):
    return test_app.test_client()

def test_login_success(client):
    response = client.post('/login', data=dict(
        username='testuser',
        password='testpassword'
    ), follow_redirects=True)

    assert b'Welcome, testuser!' in response.data

def test_logout(client):
    client.post('/login', data=dict(
        username='testuser',
        password='testpassword'
    ), follow_redirects=True)

    response = client.get('/logout', follow_redirects=True)

    assert b'You have been logged out.' in response.data
