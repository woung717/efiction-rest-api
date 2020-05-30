from requests import get, post, delete  
import json
import datetime

url = 'http://api-server-addr:8080'
headers = { 'Content-Type': 'application/json; charset=UTF-8', \
			'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36', \
			'Accept': '*/*', \
			'Accept-Encoding': 'gzip, deflate, br', \
			'Accept-Language': 'ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,zh-TW;q=0.6,zh-CN;q=0.5,zh;q=0.4'}

userId = 'userId' + datetime.datetime.now().strftime('%Y-%m-%d_%H:%M:%S')
userPassword = 'userPassword' + datetime.datetime.now().strftime('%Y-%m-%d_%H:%M:%S')

def user_api_test():
	signup_data = {'userId': userId, 'userPassword': userPassword }

	# 403 response tests
	resp = get(url + '/user/logout', headers=headers)
	assert resp.status_code == 403

	resp = post(url + '/user/withdraw', headers=headers, data=json.dumps(signup_data))
	assert resp.status_code == 403


	# Sign up test
	resp = post(url + '/user/signup', headers=headers, data=json.dumps(signup_data))
	
	assert resp.status_code == 200
	resp_body = resp.json()
	assert resp_body['userId'] == userId and resp_body['token'] != None

	headers['X-Auth-Token'] = resp_body['token']

	resp = post(url + '/user/signup', headers=headers, data=json.dumps(signup_data))
	
	assert resp.status_code == 200
	resp_body = resp.json()
	assert resp_body['userId'] == userId and resp_body['token'] == None


	# Log out test before login
	resp = get(url + '/user/logout', headers=headers)
	
	assert resp.status_code == 200
	resp_body = resp.json()
	assert resp_body['result'] == 'success'

	headers.pop('X-Auth-Token')


	# Log in test 
	resp = post(url + '/user/login', headers=headers, data=json.dumps(signup_data))
	assert resp.status_code == 200
	resp_body = resp.json()
	assert resp_body['userId'] == userId and resp_body['token'] != None

	headers['X-Auth-Token'] = resp_body['token']


	# Withdraw test
	resp = post(url + '/user/withdraw', headers=headers, data=json.dumps(signup_data))
	assert resp.status_code == 200
	resp_body = resp.json()
	assert resp_body['result'] == 'success'

	headers.pop('X-Auth-Token')


def post_api_test():
	signup_data = {'userId': userId, 'userPassword': userPassword }

	# 403 response tests
	resp = get(url + '/post/list', headers=headers)
	assert resp.status_code == 403

	resp = get(url + '/post/1', headers=headers)
	assert resp.status_code == 403

	resp = delete(url + '/post/1', headers=headers)
	assert resp.status_code == 403


	# Sign up before post tests
	resp = post(url + '/user/signup', headers=headers, data=json.dumps(signup_data))
	
	assert resp.status_code == 200
	resp_body = resp.json()
	assert resp_body['userId'] == userId and resp_body['token'] != None

	headers['X-Auth-Token'] = resp_body['token']


	# Post commit test
	articles = [{'subject': 'subject-' + datetime.datetime.now().strftime('%Y-%m-%d_%H:%M:%S_%f'), \
				'contents': 'contents-' + datetime.datetime.now().strftime('%Y-%m-%d_%H:%M:%S_%f') * 10} for _ in range(10)]
	articles_saved = []

	for a in articles:
		resp = post(url + '/post/commit', headers=headers, data=json.dumps(a))
		
		assert resp.status_code == 200
		resp_body = resp.json()
		assert resp_body['contents'] == a['contents'] and resp_body['subject'] == a['subject']
		articles_saved.append(resp_body)

	articles_updated = []

	for acls in articles_saved:
		acls['subject'] = 'subject-edited'
		acls['contents'] = 'contents-edited'

		resp = post(url + '/post/commit', headers=headers, data=json.dumps(acls))
		assert resp.status_code == 200
		resp_body = resp.json()
		assert resp_body['contents'] == acls['contents'] and resp_body['subject'] == acls['subject']
		articles_updated.append(resp_body)


	# Post listing test
	resp = get(url + '/post/list', headers=headers)
	
	assert resp.status_code == 200
	resp_body = resp.json()
	assert len(resp_body) == len(articles_updated)

	post_meta_list = resp_body[:]


	# Post retrieving test
	for p in post_meta_list:
		resp = get(url + '/post/%d' % p['id'], headers=headers)
		assert resp.status_code == 200
		resp_body = resp.json()
		assert resp_body['subject'] == p['subject']
	

	# Post deleting test
	for p in post_meta_list:
		resp = delete(url + '/post/%d' % p['id'], headers=headers)
		assert resp.status_code == 200
		resp_body = resp.json()
		assert resp_body['result'] == 'success'

	resp = get(url + '/post/list', headers=headers)
	
	assert resp.status_code == 200
	resp_body = resp.json()
	assert len(resp_body) == 0


	# Clear up account
	resp = post(url + '/user/withdraw', headers=headers, data=json.dumps(signup_data))
	assert resp.status_code == 200
	resp_body = resp.json()
	assert resp_body['result'] == 'success'

	headers.pop('X-Auth-Token')

def main():
	user_api_test()
	post_api_test()

if __name__ == '__main__':
	main()
